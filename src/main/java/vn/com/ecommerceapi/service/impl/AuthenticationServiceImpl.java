package vn.com.ecommerceapi.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.entity.UserProfileOTP;
import vn.com.ecommerceapi.enums.OTPTypeEnum;
import vn.com.ecommerceapi.exception.AuthenticationException;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.mapper.AuthenticationMapper;
import vn.com.ecommerceapi.model.request.LoginRequest;
import vn.com.ecommerceapi.model.request.RefreshTokenRequest;
import vn.com.ecommerceapi.model.request.RegisterRequest;
import vn.com.ecommerceapi.model.response.LoginResponse;
import vn.com.ecommerceapi.model.response.RolesUserResponse;
import vn.com.ecommerceapi.proxy.KeycloakProxy;
import vn.com.ecommerceapi.redis.TokenRedisService;
import vn.com.ecommerceapi.repositories.UserProfileOTPRepository;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.AuthenticationService;
import vn.com.ecommerceapi.service.KeycloakService;
import vn.com.ecommerceapi.service.UserProfileService;
import vn.com.ecommerceapi.utils.JWTUtils;
import vn.com.ecommerceapi.utils.PasswordUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private static final int TOTAL_FALSE_OTP = 5;
    private static final String ROLE_USER = "ROLE_USER";

    private final KeycloakProxy keycloakProxy;
    private final KeycloakService keycloakService;
    private final TokenRedisService tokenRedisService;
    private final UserProfileService userProfileService;
    private final AuthenticationMapper authenticationMapper;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileOTPRepository userProfileOTPRepository;
    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Override
    public LoginResponse login(LoginRequest request) {
        /* Validate username password request */
        if (StringUtils.isNullOrEmpty(request.getUsername()) || StringUtils.isNullOrEmpty(request.getPassword())) {
            throw new BusinessException("Tài khoản hoặc mật khẩu không được để trống");
        }

        String username = request.getUsername();

        /* Tìm kiếm trong DB xem có tồn tại username này không */
        UserProfile userProfile = userProfileService.findUserProfileByUsername(username);
        LOGGER.info("[AUTHENTICATION][{}][LOGIN] User Profile: {}", username, userProfile);

        if (Objects.isNull(userProfile)) {
            throw new BusinessException("Tài khoản chưa được đăng ký, vui lòng đăng ký tài khoản để tiếp tục.");
        }

        if (Boolean.FALSE.equals(userProfile.getIsActive())) {
            throw new BusinessException("Tài khoản của bạn đã bị khoá. Vui lòng liên hệ quản trị viên để được hỗ trợ");
        }

        /* So sánh 2 password xem có trùng nhau hay không */
        if (StringUtils.notEquals(userProfile.getPassword(), PasswordUtils.endCodeMD5(request.getPassword()))) {
            throw new BusinessException("Sai tên tài khoản hoặc mật khẩu. Vui lòng thử lại.");
        }

        /* Logout tài khoản hiện tại */
        logout(username);

        Keycloak keyCloak = keycloakService.getKeycloak(username, request.getPassword());

        AccessTokenResponse accessTokenKeycloak = keyCloak.tokenManager().getAccessToken();

        DecodedJWT jwtDecode = JWT.decode(accessTokenKeycloak.getToken());
        List<String> roles = jwtDecode.getClaim("realm_access").as(RolesUserResponse.class).getRoles().stream().filter(role -> role.startsWith("ROLE")).toList();
        LOGGER.info("[AUTHENTICATION][{}][LOGIN] Roles: {}", username, roles);

        if (roles.isEmpty()) {
            throw new AuthenticationException("Bạn không có quyền truy cập chức năng này. Vui lòng đăng nhập lại", 403);
        }

        /* Lưu token vào redis theo thời gian hết hạn token */
        saveTokenToRedis(username, accessTokenKeycloak);
        LOGGER.info("[AUTHENTICATION][{}][LOGIN] Lưu thông tin Token vào Redis thành công", username);

        LOGGER.info("[AUTHENTICATION][{}][LOGIN] Login Success", username);
        return authenticationMapper.mapToLoginResponse(accessTokenKeycloak, roles);
    }

    @Override
    public void register(RegisterRequest request) {
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

        int countVerifyFail = Objects.isNull(userProfileOTP.getCountVerifyFalse()) ? 0 : userProfileOTP.getCountVerifyFalse();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER] Số lần verify OTP lỗi: {}", username, countVerifyFail);

        if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP) {
            throw new BusinessException(Constant.VERIFY_OTP_5TH);
        }

        /* Kiểm tra xem có trùng otp hay không */
        saveUserProfileOTP(request, userProfileOTP, countVerifyFail);

        UserRepresentation userRepresentation = buildUserRepresentation(request);
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_REPRESENTATION][{}]", username, userRepresentation);

        /* Lấy keycloak */
        Keycloak keycloakInstance = keycloakService.getKeycloakByClient();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][KEYCLOAK_INSTANCE][{}]", username, keycloakInstance);

        UsersResource usersResource = keycloakInstance.realm(keycloakSpringBootProperties.getRealm()).users();
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_RESOURCE][{}]", username, usersResource);

        try (Response userCreateResponse = usersResource.create(userRepresentation)) {
            LOGGER.info("[AUTHENTICATION][{}][REGISTER][USER_CREATE_RESPONSE][{}]", username, userCreateResponse);

            if (userCreateResponse.getStatus() == 201) {
                String keycloakId = CreatedResponseUtil.getCreatedId(userCreateResponse);
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
    }

    @Override
    public void logout(String username) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT] Starting.....", username);

        if (StringUtils.isNullOrEmpty(username)) {
            return;
        }
        String keycloakId = userProfileRepository.getKeycloakIdByUsername(username);
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT] Keycloak ID: {}", username, keycloakId);

        Keycloak keycloak = keycloakService.getKeycloakByClient();
        UserResource usersResource = keycloak.realm(keycloakSpringBootProperties.getRealm()).users().get(keycloakId);
        List<UserSessionRepresentation> userSessions = usersResource.getUserSessions();

        if (!userSessions.isEmpty()) {
            usersResource.logout();
            keycloak.close();
        }

        /* Xoá key ở redis */
        tokenRedisService.remove(username);

        LOGGER.info("[AUTHENTICATION][{}][LOGOUT] Logout Success", username);
    }

    @Override
    public LoginResponse refresh(RefreshTokenRequest request) {
        validateRequest(request);

        String username = request.getUsername();

        validateRefreshToken(request, username);

        AccessTokenResponse accessTokenResponse = keycloakProxy.refreshToken(request.getRefreshToken());

        /* kiểm tra xem đã refresh thành công */
        if (accessTokenResponse == null || accessTokenResponse.getToken() == null || accessTokenResponse.getRefreshToken() == null) {
            throw new AuthenticationException();
        }

        saveTokenToRedis(request.getUsername(), accessTokenResponse);
        LOGGER.info("[AUTHENTICATION][{}][REFRESH] Lưu thông tin Token vào Redis thành công", username);

        List<String> roles = JWTUtils.getRoles(accessTokenResponse.getToken());
        LOGGER.info("[AUTHENTICATION][{}][REFRESH] Roles: {}", username, roles);

        return authenticationMapper.mapToLoginResponse(accessTokenResponse, roles);
    }

    private void saveTokenToRedis(String username, AccessTokenResponse accessTokenResponse) {
        Date expiredTimeToken = JWTUtils.getExpiredTime(accessTokenResponse.getToken());
        LOGGER.info("[AUTHENTICATION][{}] Thời gian hết hạn Token: {}", username, expiredTimeToken);

        Date expiredTimeRefreshToken = JWTUtils.getExpiredTime(accessTokenResponse.getRefreshToken());
        LOGGER.info("[AUTHENTICATION][{}] Thời gian hết hạn Refresh Token: {}", username, expiredTimeRefreshToken);

        /* lưu access token mới redis */
        tokenRedisService.set(username, accessTokenResponse.getToken(), expiredTimeToken);

        /* lưu refresh token mới redis */
        tokenRedisService.setRefreshToken(username, accessTokenResponse.getRefreshToken(), expiredTimeRefreshToken);
    }

    private void validateRefreshToken(RefreshTokenRequest request, String username) {
        /* kiểm tra refresh token có tồn tại trên redis không */
        String refreshTokenRedis = tokenRedisService.getRefreshToken(request.getUsername());
        if (StringUtils.notEquals(refreshTokenRedis, request.getRefreshToken())) {
            LOGGER.info("[AUTHENTICATION][{}][REFRESH] Refresh token không tồn tại hoặc đã hết hạn", username);
            throw new AuthenticationException();
        }

        /* kiểm tra token có tồn tại trên redis không */
        String tokenRedis = tokenRedisService.get(request.getUsername());
        if (!StringUtils.isNullOrEmpty(tokenRedis)) {
            LOGGER.info("[AUTHENTICATION][{}][REFRESH] Token chưa hết hạn", username);
            throw new AuthenticationException();
        }
    }

    private static void validateRequest(RefreshTokenRequest request) {
        if (StringUtils.isNullOrEmpty(request.getRefreshToken()) || StringUtils.isNullOrEmpty(request.getUsername())) {
            throw new BusinessException("Username hoặc refresh token không được để trống");
        }
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

    private void saveUserProfileOTP(RegisterRequest request, UserProfileOTP userProfileOTP, int countVerifyFail) {
        if (StringUtils.notEquals(userProfileOTP.getOtp(), request.getOtp())) {
            userProfileOTP.setCountVerifyFalse(countVerifyFail + 1);
            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
            throw new BusinessException(Constant.OTP_EXPIRED_OR_INVALID_MES);
        } else {
            userProfileOTP.setIsVerified(true);
            userProfileOTP.setVerifyAt(LocalDateTime.now());
            userProfileOTP.setLastVerifyAt(LocalDateTime.now());
            userProfileOTPRepository.save(userProfileOTP);
        }
    }

    private static UserRepresentation buildUserRepresentation(RegisterRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(request.getUsername());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setEmailVerified(true);
        return userRepresentation;
    }
}
