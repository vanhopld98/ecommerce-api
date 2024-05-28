package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.entity.UserProfileOTP;
import vn.com.ecommerceapi.enums.OTPTypeEnum;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.model.request.LoginRequest;
import vn.com.ecommerceapi.model.request.RegisterRequest;
import vn.com.ecommerceapi.model.response.LoginResponse;
import vn.com.ecommerceapi.repositories.UserProfileOTPRepository;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.AuthenticationService;
import vn.com.ecommerceapi.service.KeycloakService;
import vn.com.ecommerceapi.service.UserProfileService;
import vn.com.ecommerceapi.utils.PasswordUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private static final int TOTAL_FALSE_OTP = 5;
    private static final String ROLE_USER = "ROLE_USER";

    private final UserProfileService userProfileService;
    private final UserProfileOTPRepository userProfileOTPRepository;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;
    private final KeycloakService keycloakService;
    private final UserProfileRepository userProfileRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        String username = request.getUsername();

        /* Tìm kiếm trong DB xem username này đã tồn tại hay chưa */
        UserProfile userProfile = userProfileService.findUserProfileByUsername(username);
        if (Objects.nonNull(userProfile)) {
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }

        /* Kiểm tra xem có đúng là người nhận được otp tạo tài khoản hay không. */
        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(username, OTPTypeEnum.REGISTER.name());
        LOGGER.info("[AUTHENTICATION][{}][REGISTER] User Profile OTP: {}", username, userProfileOTP);

        if (Objects.isNull(userProfileOTP)) {
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        }

        if (Objects.nonNull(userProfileOTP.getLastVerifyAt())) {
            throw new BusinessException(Constant.OTP_VERIFY_NULL);
        }

        int countVerifyFail = Objects.isNull(userProfileOTP.getCountVerifyFalse()) ? 0 : userProfileOTP.getCountVerifyFalse();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER] Số lần verify OTP lỗi: {}", username, countVerifyFail);

        if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP) {
            throw new BusinessException(Constant.VERIFY_OTP_5TH);
        }

        /* Kiểm tra xem có trùng otp hay không */
        if (StringUtils.notEquals(userProfileOTP.getOtp(), request.getOtp())) {
            userProfileOTP.setCountVerifyFalse(countVerifyFail + 1);
            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(true);
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_REPRESENTATION][{}]", username, userRepresentation);

        /* Lấy keycloak */
        var keycloakInstance = keycloakService.getKeycloakByClient();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][KEYCLOAK_INSTANCE][{}]", username, keycloakInstance);

        var usersResource = keycloakInstance.realm(keycloakSpringBootProperties.getRealm()).users();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_RESOURCE][{}]", username, usersResource);

        try (var userCreateResponse = usersResource.create(userRepresentation)) {
            LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_CREATE_RESPONSE][{}]", username, userCreateResponse);

            if (userCreateResponse.getStatus() == 201) {
                var keycloakId = CreatedResponseUtil.getCreatedId(userCreateResponse);
                LOGGER.info("[AUTHENTICATION][{}][REGISTER] Keycloak ID: {}", username, keycloakId);

                saveUserProfile(request, keycloakId, username);

                /* Get user từ keycloak theo id của keycloak vừa đc tạo */
                UserResource userResource = usersResource.get(keycloakId);

                /* Set password của user vào keycloak */
                userResource.resetPassword(keycloakService.credentialRepresentation(request.getPassword()));

                /* set role cho user */
                List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

                for (RoleRepresentation roleRepresentation : roleRepresentationList) {
                    if (roleRepresentation.getName().equals(ROLE_USER)) {
                        userResource.roles().realmLevel().add(List.of(roleRepresentation));
                        break;
                    }
                }

                /* Cập nhật thông tin của user vào keycloak */
                userResource.update(new UserRepresentation());
            } else if (userCreateResponse.getStatus() == 409) {
                LOGGER.info("[AUTHENTICATION][{}][REGISTER] User đã tồn tại trên Keycloak", username);
                throw new BusinessException(Constant.USERNAME_EXISTS);
            } else {
                LOGGER.info("[AUTHENTICATION][{}][REGISTER] Có lỗi trong quá trình thêm mới user trên Keycloak. Status Code Keycloak: {}", username, userCreateResponse.getStatus());
                throw new BusinessException(Constant.ERROR_REGISTER_USER);
            }
        }

        /* Inactive toàn bộ OTP của user */
        userProfileOTPRepository.inactiveAllStatus(username, OTPTypeEnum.REGISTER.name());

        /* Thực hiện login để trả ra token mới nhất của user */
        return login(LoginRequest.builder().username(request.getUsername()).password(request.getPassword()).build());
    }

    @Override
    public void logout(String username) {
        // do nothing
    }

    private void saveUserProfile(RegisterRequest request, String keycloakId, String username) {
        UserProfile userProfileCreate = UserProfile
                .builder()
                .address(request.getAddress())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .keycloakId(keycloakId)
                .username(username)
                .isActive(true)
                .phoneNumber(request.getPhoneNumber())
                .password(PasswordUtils.endCodeMD5(request.getPassword()))
                .build();
        userProfileRepository.save(userProfileCreate);
    }
}
