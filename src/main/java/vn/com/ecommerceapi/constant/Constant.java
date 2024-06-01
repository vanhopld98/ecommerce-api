package vn.com.ecommerceapi.constant;

public class Constant {

    private Constant() {
    }

    public static final String EXCEPTION_MESSAGE_DEFAULT = "Dịch vụ tạm thời gián đoạn. Vui lòng thử lại sau ít phút.";
    public static final String USERNAME_EXISTS = "Tài khoản đã được sử dụng. Vui lòng thử lại bằng tài khoản khác.";
    public static final String OTP_EXPIRED_OR_INVALID_MES = "Mã OTP chưa chính xác. Vui lòng kiểm tra lại.";
    public static final String OTP_VERIFY_NULL = "Vui lòng xác thực OTP để đăng kí tài khoản";
    public static final String VERIFY_OTP_5TH = "Mã xác thực đã bị hủy. Bạn chỉ có thể lấy mã xác thực mới sau 5 phút nữa.";
    public static final String PASSWORD_DEFAULT = "123456aA";
    public static final String ERROR_REGISTER_USER = "Có lỗi trong quá trình đăng ký tài khoản. Vui lòng thử lại.";
    public static final String OTP_EXCEEDED = "Mã xác thực chỉ được gửi tối đa 3 lần trong 1 ngày";
    public static final String VERIFY_OTP_BLOCKED_MESS = "Bạn đã nhập sai mã OTP %s lần liên tiếp, vui lòng thử lại sau 5 phút.";
    public static final String USER_NOT_EXIST = "Tài khoản không tồn tại. Vui lòng kiểm tra lại.";


}
