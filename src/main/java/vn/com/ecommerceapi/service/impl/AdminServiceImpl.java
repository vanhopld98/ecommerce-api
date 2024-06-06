package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.mapper.UserProfileMapper;
import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.model.response.UserProfilesResponse;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.AdminService;
import vn.com.ecommerceapi.utils.JWTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfilesResponse getUserProfiles(int page, int size) {
        String username = JWTUtils.getUsername();

        List<UserProfile> userProfiles = userProfileRepository.findUserProfilesOrderByCreatedAtDesc();
        LOGGER.info("[ADMIN][{}][GET USERS] User Profiles: {}", username, userProfiles);

        int totalPage = getTotalPage(size, userProfiles);
        LOGGER.info("[ADMIN][{}][GET USERS] Tổng số trang: {}", username, totalPage);

        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
        for (UserProfile userProfile : userProfiles.stream().skip((long) page * size).limit(size).toList()) {
            UserProfileResponse userProfileResponse = userProfileMapper.mapToUserProfileResponse(userProfile);
            userProfileResponses.add(userProfileResponse);
        }

        LOGGER.info("[ADMIN][{}][GET USERS] User Profiles Response: {}", username, userProfileResponses);
        return UserProfilesResponse.builder()
                .page(page)
                .size(size)
                .total(userProfiles.size())
                .userProfiles(userProfileResponses)
                .build();
    }

    private static int getTotalPage(int size, List<UserProfile> userProfiles) {
        int totalPage = (int) Math.ceil((double) userProfiles.size() / size);
        if (totalPage == 0) {
            return 1;
        }
        return totalPage;
    }

    @Override
    public UserProfileResponse getUserByKeycloakId(String keycloakId) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[ADMIN][{}][GET USER] Keycloak ID: {}", username, keycloakId);

        Optional<UserProfile> userProfileOptional = userProfileRepository.findByKeycloakId(keycloakId);
        if (userProfileOptional.isEmpty()) {
            LOGGER.error("[ADMIN][{}][GET USER] User không tồn tại trong hệ thống", username);
            throw new BusinessException(Constant.USER_NOT_EXIST);
        }

        UserProfile userProfile = userProfileOptional.get();
        LOGGER.info("[ADMIN][{}][GET USER] User Profile: {}", username, userProfile);

        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

}
