package vn.com.ecommerceapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private String username;

    private String password;

    private String otp;

}
