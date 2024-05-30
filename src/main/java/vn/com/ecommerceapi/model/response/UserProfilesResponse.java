package vn.com.ecommerceapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilesResponse {

    private int page;
    private int size;
    private int totalPage;
    private List<UserProfileResponse> userProfiles;

}
