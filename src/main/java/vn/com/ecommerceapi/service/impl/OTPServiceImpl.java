package vn.com.ecommerceapi.service.impl;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPServiceImpl.class);

    private static final String CONTENT = "<h2>Mã xác thực đăng ký tại Ecommerce</h2><p>Xin chào: %s,</p><p>Mã OTP của bạn là: <strong>%s</strong></p><p>Vui lòng sử dụng mã OTP này để xác thực đăng ký tài khoản của bạn</p><p>Lưu ý không chia sẻ mã OTP này cho bất kỳ ai.</p><br><p>Trân trọng,</p><p>Ecommerce</p>";
    private static final String SUBJECT = "Mã xác thực đăng ký tài khoản tại Ecommerce";
    private static final int TOTAL_RETRY_PER_DAY = 3;
    private static final int TOTAL_FALSE_OTP = 5;

    private final UserProfileOTPRepository userProfileOTPRepository;
    private final UserProfileRepository userProfileRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String usernameEmail;

    @Override
    public void sendOTP(SendOTPRequest request) {
        /* Kiểm tra Email có đúng định dạng hay không, và username */
        validateRequest(request);

        String username = request.getUsername();

        /* Nếu tồn tại thông tin user thì dừng luồng */
        validateUserExist(username);

        /* Kiểm tra số lần gửi OTP trong 1 ngày */
        validateTotalOTPInDay(username);

        /* Kiểm tra đã quá số lần verify fail hay chưa */
        validateCountVerifyOTP(request);

        /* Inactive tất cả OTP đang còn hiệu lực nếu cấp mới OTP */
        userProfileOTPRepository.inactiveAllStatus(username, OTPTypeEnum.REGISTER.name());

        String otp = generateOTP();
        LOGGER.info("[OTP][{}][SEND OTP] Mã OTP: {}", username, otp);

        /* Gửi Email đến người dùng */
        sendEmail(request, username, otp);

        /* Lưu thông tin OTP để verify */
        saveUserProfileOTP(request, otp);
    }

    private static void validateRequest(SendOTPRequest request) {
        if (StringUtils.isNullOrEmpty(request.getEmail()) || RegexUtils.matches(request.getEmail(), RegexUtils.EMAIL)) {
            throw new BusinessException("Email không hợp lệ");
        }

        if (StringUtils.isNullOrEmpty(request.getUsername())) {
            throw new BusinessException("Username không được để trống");
        }
    }

    private void validateUserExist(String username) {
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUsername(username);
        if (userProfileOptional.isPresent()) {
            throw new BusinessException(Constant.USERNAME_EXISTS, "USERNAME_EXISTS");
        }
    }

    private void validateCountVerifyOTP(SendOTPRequest request) {
        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(request.getUsername(), OTPTypeEnum.REGISTER.name());
        if (Objects.nonNull(userProfileOTP)) {
            int countVerifyFail = userProfileOTP.getCountVerifyFalse() == null ? 0 : userProfileOTP.getCountVerifyFalse();
            if (Boolean.FALSE.equals(userProfileOTP.getStatus())
                    && Objects.nonNull(userProfileOTP.getLastVerifyAt())
                    && countVerifyFail >= TOTAL_FALSE_OTP
                    && compareTimeVerify(userProfileOTP.getLastVerifyAt())) {
                throw new BusinessException(String.format(Constant.VERIFY_OTP_BLOCKED_MESS, TOTAL_FALSE_OTP));
            }
        }
    }

    private void validateTotalOTPInDay(String username) {
        int totalOTPToday = userProfileOTPRepository.getTotalOTPToday(username, OTPTypeEnum.REGISTER.name());
        if (totalOTPToday >= TOTAL_RETRY_PER_DAY) {
            throw new BusinessException(Constant.OTP_EXCEEDED);
        }
    }

    private boolean compareTimeVerify(LocalDateTime verifyAt) {
        /* Lấy thời gian hiện tại */
        LocalDateTime now = LocalDateTime.now();

        /* Tính khoảng cách thời gian giữa thời gian hiện tại và thời gian cần so sánh */
        Duration duration = Duration.between(verifyAt, now);

        /* Kiểm tra xem khoảng cách này có quá 5 phút hay không */
        return Math.abs(duration.toMillis()) <= 300000;
    }

    private void sendEmail(SendOTPRequest request, String username, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(usernameEmail));
            message.setRecipients(Message.RecipientType.TO, request.getEmail());
            message.setSubject(SUBJECT);
            message.setContent(String.format(CONTENT, request.getUsername(), otp), "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error("[OTP][{}][SEND OTP] Có lỗi khi gửi Email. Exception: {}", username, e.getMessage());
            throw new BusinessException(Constant.EXCEPTION_MESSAGE_DEFAULT);
        }
    }

    private void saveUserProfileOTP(SendOTPRequest request, String otp) {
        UserProfileOTP userProfileOtp = new UserProfileOTP();
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
