package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.request.LoginRequest;
import vn.com.ecommerceapi.model.request.RegisterRequest;
import vn.com.ecommerceapi.model.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest loginRequest);

    LoginResponse register(RegisterRequest registerRequest);

    void logout(String username);

}
