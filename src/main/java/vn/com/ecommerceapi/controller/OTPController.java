package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.model.request.SendOTPRequest;
import vn.com.ecommerceapi.service.OTPService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OTPController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPController.class);

    private final OTPService otpService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendOTP(@RequestBody SendOTPRequest request) {
        otpService.sendOTP(request);
        return ResponseEntity.ok().build();
    }
}
