package vn.com.ecommerceapi.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.model.response.UserProfileResponse;

@Component
@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse convertToUserProfileResponse(UserProfile userProfile);

}
