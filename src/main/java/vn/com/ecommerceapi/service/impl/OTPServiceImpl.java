package vn.com.ecommerceapi.service.impl;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.entity.UserProfile;
import vn.com.ecommerceapi.entity.UserProfileOTP;
import vn.com.ecommerceapi.enums.OTPTypeEnum;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.model.request.SendOTPRequest;
import vn.com.ecommerceapi.repositories.UserProfileOTPRepository;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.OTPService;
import vn.com.ecommerceapi.utils.RegexUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPServiceImpl.class);

    private static final String CONTENT = "<h2>Mã xác thực đăng ký tại Ecommerce</h2><p>Xin chào: %s,</p><p>Mã OTP của bạn là: <strong>%s</strong></p><p>Vui lòng sử dụng mã OTP này để xác thực đăng ký tài khoản của bạn</p><p>Lưu ý không chia sẻ mã OTP này cho bất kỳ ai.</p><br><p>Trân trọng,</p><p>Ecommerce</p>";
    private static final String SUBJECT = "Mã xác thực đăng ký tài khoản tại Ecommerce";

    private final UserProfileOTPRepository userProfileOTPRepository;
    private final UserProfileRepository userProfileRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String usernameEmail;

    @Override
    public void sendOTP(SendOTPRequest request) {
        if (StringUtils.isNullOrEmpty(request.getEmail()) || RegexUtils.matches(request.getEmail(), RegexUtils.EMAIL)) {
            throw new BusinessException("Email không hợp lệ");
        }

        if (StringUtils.isNullOrEmpty(request.getUsername())) {
            throw new BusinessException("Username không được để trống");
        }
        String username = request.getUsername();

        /* Nếu tồn tại thông tin user thì dừng luồng */
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUsername(username);
        if (userProfileOptional.isPresent()) {
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }

        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(request.getUsername(), OTPTypeEnum.REGISTER.name());
        if (Objects.nonNull(userProfileOTP)) {

        }

        MimeMessage message = mailSender.createMimeMessage();

        var otp = generateOTP();
        LOGGER.info("[OTP][{}] Tạo otp: {}", username, otp);

        try {
            message.setFrom(new InternetAddress(usernameEmail));

            /* */
            message.setRecipients(Message.RecipientType.TO, request.getEmail());
            message.setSubject(SUBJECT);
            message.setContent(String.format(CONTENT, request.getUsername(), otp), "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException(e.getMessage());
        }

        var userProfileOtp = new UserProfileOTP();
        userProfileOtp.setOtp(otp);
        userProfileOtp.setUsername(request.getUsername());
        userProfileOtp.setType(StringUtils.isNullOrEmpty(request.getType()) ? OTPTypeEnum.REGISTER.name() : request.getType());
        userProfileOtp.setStatus(true);
        userProfileOtp.setCountVerifyFalse(0);
        userProfileOTPRepository.save(userProfileOtp);
    }

    private static String generateOTP() {
        int otp = new SecureRandom().nextInt(999999);
        return String.format("%06d", otp);
    }
}
