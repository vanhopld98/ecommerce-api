package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.aop.Secured;
import vn.com.ecommerceapi.enums.RoleEnum;
import vn.com.ecommerceapi.model.response.UserProfileResponse;
import vn.com.ecommerceapi.model.response.UserProfilesResponse;
import vn.com.ecommerceapi.service.AdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/users")
    public ResponseEntity<UserProfilesResponse> getUsers(@RequestParam(required = false) int page,
                                                         @RequestParam(required = false) int size) {
        return ResponseEntity.ok(adminService.getUserProfiles(page, size));
    }

    @Secured(role = RoleEnum.ADMIN)
    @GetMapping("/user/{keycloakId}")
    public ResponseEntity<UserProfileResponse> getUserByKeycloakId(@PathVariable(name = "keycloakId") String keycloakId) {
        return ResponseEntity.ok(adminService.getUserByKeycloakId(keycloakId));
    }

}
