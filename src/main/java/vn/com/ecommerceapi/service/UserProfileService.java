package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.entity.UserProfile;

public interface UserProfileService {

    UserProfile findUserProfileByUsername(String username);

}
