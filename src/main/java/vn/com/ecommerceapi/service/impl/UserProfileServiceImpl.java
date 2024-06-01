package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.mapper.UserProfileMapper;
import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.UserProfileService;
import vn.com.ecommerceapi.utils.JWTUtils;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile findUserProfileByUsername(String username) {
        return userProfileRepository.findByUsername(username).orElse(null);
    }

    @Override
    public UserProfileResponse myProfile() {
        String username = JWTUtils.getUsername();
        UserProfile userProfile = findUserProfileByUsername(username);
        return userProfileMapper.mapToUserProfileResponse(userProfile);
    }

}
