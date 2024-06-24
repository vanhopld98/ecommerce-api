package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.aop.Secured;
import vn.com.ecommerceapi.enums.RoleEnum;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.service.UserProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOGGER = LoggingFactory.getLogger(UserController.class);

    private final UserProfileService userProfileService;

    @Secured(role = RoleEnum.USER)
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() {
        return ResponseEntity.ok(userProfileService.myProfile());
    }


}
