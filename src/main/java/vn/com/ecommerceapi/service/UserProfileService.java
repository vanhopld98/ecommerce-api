package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.model.response.UserProfileResponse;

public interface UserProfileService {

    UserProfile findUserProfileByUsername(String username);

    UserProfileResponse myProfile();
}
