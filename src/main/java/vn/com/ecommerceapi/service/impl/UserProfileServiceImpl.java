package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.UserProfileService;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfile findUserProfileByUsername(String username) {
        return userProfileRepository.findByUsername(username).orElse(null);
    }

}
