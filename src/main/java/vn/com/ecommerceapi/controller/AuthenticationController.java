package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.model.request.LoginRequest;
import vn.com.ecommerceapi.model.request.LogoutRequest;
import vn.com.ecommerceapi.model.request.RefreshTokenRequest;
import vn.com.ecommerceapi.model.request.RegisterRequest;
import vn.com.ecommerceapi.model.response.LoginResponse;
import vn.com.ecommerceapi.service.AuthenticationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REGISTER][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGIN][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][LOGOUT][STARTING...] Request: {}", request.getUsername(), request);
        authenticationService.logout(request.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        LOGGER.info("[AUTHENTICATION][{}][REFRESH][STARTING...] Request: {}", request.getUsername(), request);
        return ResponseEntity.ok(authenticationService.refresh(request));
    }
}
