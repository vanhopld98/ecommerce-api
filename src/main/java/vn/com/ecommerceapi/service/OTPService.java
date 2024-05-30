package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.request.SendOTPRequest;

public interface OTPService {

    void sendOTP(SendOTPRequest request);
}
