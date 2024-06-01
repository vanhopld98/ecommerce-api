package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.model.response.UserProfilesResponse;

public interface AdminService {

    UserProfilesResponse getUserProfiles(int page, int size);

    UserProfileResponse getUserByKeycloakId(String keycloakId);
}
