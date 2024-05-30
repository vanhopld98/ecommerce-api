package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.mapper.UserProfileMapper;
import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.model.response.UserProfilesResponse;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.AdminService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfilesResponse getUserProfiles(int page, int size) {
        List<UserProfile> userProfiles = userProfileRepository.findUserProfilesOrderByCreatedAtDesc().stream().skip((long) page * size).limit(size).toList();
        int totalPage = (int) Math.ceil((double) userProfiles.size() / size);
        if (totalPage == 0) {
            totalPage = 1;
        }

        List<UserProfileResponse> userProfileResponses = new ArrayList<>();
        for (UserProfile userProfile : userProfiles) {
            UserProfileResponse userProfileResponse = userProfileMapper.convertToUserProfileResponse(userProfile);
            userProfileResponses.add(userProfileResponse);
        }
        return UserProfilesResponse.builder()
                .page(page)
                .size(size)
                .totalPage(totalPage)
                .userProfiles(userProfileResponses)
                .build();
    }

}
