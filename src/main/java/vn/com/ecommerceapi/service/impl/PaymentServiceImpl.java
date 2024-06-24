package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.entity.Cart;
import vn.com.ecommerceapi.entity.PaymentVNPay;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.model.response.VNPayInitOrderResponse;
import vn.com.ecommerceapi.repositories.CartRepository;
import vn.com.ecommerceapi.repositories.PaymentVNPayRepository;
import vn.com.ecommerceapi.repositories.ProductRepository;
import vn.com.ecommerceapi.service.PaymentService;
import vn.com.ecommerceapi.utils.DateUtils;
import vn.com.ecommerceapi.utils.HMacUtils;
import vn.com.ecommerceapi.utils.HttpsUtils;
import vn.com.ecommerceapi.utils.JWTUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static jodd.util.StringPool.UNDERSCORE;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LoggingFactory.getLogger(PaymentServiceImpl.class);
    public static final String VND = "VND";
    public static final String OTHER = "other";
    public static final String VN = "vn";
    public static final String VNBANK = "VNBANK";

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PaymentVNPayRepository paymentVNPayRepository;

    public static final String VERSION_PARAM = "vnp_Version";
    public static final String COMMAND_PARAM = "vnp_Command";
    public static final String EXPIRE_DATE_PARAM = "vnp_ExpireDate";
    public static final String CREATE_DATE_PARAM = "vnp_CreateDate";
    public static final String IP_ADDR_PARAM = "vnp_IpAddr";
    public static final String RETURN_URL_PARAM = "vnp_ReturnUrl";
    public static final String LOCALE_PARAM = "vnp_Locale";
    public static final String ORDER_TYPE_PARAM = "vnp_OrderType";
    public static final String ORDER_INFO_PARAM = "vnp_OrderInfo";
    public static final String TXN_REF_PARAM = "vnp_TxnRef";
    public static final String BANK_CODE_PARAM = "vnp_BankCode";
    public static final String TMN_CODE_PARAM = "vnp_TmnCode";
    public static final String AMOUNT_PARAM = "vnp_Amount";
    public static final String CURR_CODE_PARAM = "vnp_CurrCode";
    public static final String SECURE_HASH_PARAM = "vnp_SecureHash";

    @Value("${vnpay.version}")
    private String version;

    @Value("${vnpay.command}")
    private String command;

    @Value("${vnpay.tmn-code}")
    private String tmnCode;

    @Value("${vnpay.domain}")
    private String domain;

    @Value("${vnpay.hash-secret}")
    private String hashSecret;

    @Value("${vnpay.callback-url}")
    private String callbackUrl;

    @Override
    public VNPayInitOrderResponse initOrder() {
        String username = JWTUtils.getUsername();

        /*-- Validate số tiền thanh toán --*/
        List<Cart> carts = cartRepository.findAllByUsernameOrderByCreatedAtDesc(username);
        if (CollectionUtils.isEmpty(carts)) {
            throw new BusinessException("Không tìm thấy thông tin đơn hàng");
        }

        List<BigDecimal> prices = new ArrayList<>();
        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                BigDecimal price = new BigDecimal(cart.getQuantity()).multiply(product.getPrice());
                prices.add(price);
            }
        }
        int totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Tổng số tiền của đơn hàng: {}", username, totalPrice);

        if (totalPrice <= 0) {
            throw new BusinessException("Giá trị đơn hàng không hợp lệ");
        }

        /*-- Mã đơn hàng --*/
        String invoiceNo = "ECOMMERCE" + UNDERSCORE + "VNPAY" + UNDERSCORE + username.toUpperCase() + UNDERSCORE + (int) (System.currentTimeMillis() / 1000);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Mã đơn hàng thanh toán: {}", username, invoiceNo);

        /*-- Thông tin mô  tả đơn hàng --*/
        String orderInfo = "ECommerce - Thanh toan don hang: " + invoiceNo;
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Mô tả đơn hàng thanh toán: {}", username, invoiceNo);

        /*-- Địa chỉ IP của khách hàng --*/
        String ipAddress = StringUtils.isNullOrEmpty(HttpsUtils.getValueFromHeader(Constant.X_ORIGIN_FORWARDED_FOR)) ? "127.0.0.1" : HttpsUtils.getValueFromHeader(Constant.X_ORIGIN_FORWARDED_FOR);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Địa chỉ IP V4: {}", username, ipAddress);

        /*-- Thời gian khởi tạo đơn hàng --*/
        String createDate = DateUtils.getDate(DateUtils.YYYYMMDDHHMMSS);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thời gian tạo đơn hàng: {}", username, createDate);

        /*-- Thời gian hết hạn đơn hàng --*/
        String expireDate = DateUtils.getExpireDate(createDate, DateUtils.YYYYMMDDHHMMSS, 15);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thời gian hết hạn đơn hàng: {}", username, expireDate);

        /*-- Số tiền khi gửi sang VN Pay thì phải nhân với 100 --*/
        HashMap<String, String> vnpayParams = addParamsToMap(totalPrice, invoiceNo, orderInfo, createDate, expireDate, ipAddress);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thông tin param khi đưa vào map: {}", username, vnpayParams);

        ArrayList<String> fieldNames = new ArrayList<>(vnpayParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        /*-- Encode về US_ASCII và build ra query url của đơn hàng --*/
        encodeAndBuildQueryUrl(vnpayParams, fieldNames, hashData, query);

        /*-- Thông tin query url của đơn hàng --*/
        String queryUrl = query.toString();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Thông tin đường link query đơn hàng: {}", username, queryUrl);

        /*-- Chữ ký của đơn hàng --*/
        String secureHash = HMacUtils.encodeHMacSHA512(hashSecret, hashData.toString());
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Secure Hash của đơn hàng: {}", username, secureHash);

        /*-- Đường link thanh toán của đơn hàng --*/
        String target = UriComponentsBuilder.fromHttpUrl(domain).query(queryUrl).queryParam(SECURE_HASH_PARAM, secureHash).build().toString();
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Đường link thanh toán: {}", username, target);

        /*-- Lưu lại các thông tin thanh toán --*/
        /*-- Lưu lịch sử thanh toán --*/
//        var paymentHistory = buildPaymentHistory(request, username, device, invoiceNo);

        /*-- Lưu thông tin giao dịch vnpay --*/
        var paymentVNPay = buildPaymentVNPay(totalPrice, expireDate, username, invoiceNo, orderInfo, createDate, ipAddress);

        /*-- Ở đây do chưa tìm được lỗi vì sao khi lưu thông tin vào databse thì mất thông tin header, nên sẽ get thông tin ra 1 lượt rồi mới lưu --*/
//        paymentHistoryRepository.save(paymentHistory);
//        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Lưu thông tin lịch sử thanh toán: {}", username, paymentHistory);

        paymentVNPayRepository.save(paymentVNPay);
        LOGGER.info("[PAYMENT][VNPAY][INIT][{}] Lưu thông tin thanh toán VNPay: {}", username, paymentVNPay);

        return VNPayInitOrderResponse.builder().target(target).txnRef(invoiceNo).build();
    }

    private PaymentVNPay buildPaymentVNPay(int amount, String expiredDate, String username, String invoiceNo, String orderInfo, String createDate, String ipAddress) {
        var paymentVNPay = new PaymentVNPay();
        paymentVNPay.setUsername(username);
        paymentVNPay.setTransactionExpiredDate(createDate);
        paymentVNPay.setTransactionCreateDate(expiredDate);
        paymentVNPay.setStatus("INIT SUCCESS");
        paymentVNPay.setCardType(VNBANK);
        paymentVNPay.setInvoiceNo(invoiceNo);
        paymentVNPay.setDescription(orderInfo);
        paymentVNPay.setAmount(BigInteger.valueOf(amount));
        paymentVNPay.setIpAddress(ipAddress);
        return paymentVNPay;
    }

    private HashMap<String, String> addParamsToMap(int amount, String invoiceNo, String orderInfo, String createDate, String expireDate, String ipAddress) {
        var vnpayParams = new HashMap<String, String>();
        vnpayParams.put(VERSION_PARAM, version);
        vnpayParams.put(COMMAND_PARAM, command);
        vnpayParams.put(TMN_CODE_PARAM, tmnCode);
        vnpayParams.put(AMOUNT_PARAM, String.valueOf(amount * 100));
        vnpayParams.put(CURR_CODE_PARAM, VND);
        vnpayParams.put(BANK_CODE_PARAM, VNBANK);
        vnpayParams.put(TXN_REF_PARAM, invoiceNo);
        vnpayParams.put(ORDER_INFO_PARAM, orderInfo);
        vnpayParams.put(ORDER_TYPE_PARAM, OTHER);
        vnpayParams.put(LOCALE_PARAM, VN);
        vnpayParams.put(RETURN_URL_PARAM, callbackUrl);
        vnpayParams.put(IP_ADDR_PARAM, ipAddress);
        vnpayParams.put(CREATE_DATE_PARAM, createDate);
        vnpayParams.put(EXPIRE_DATE_PARAM, expireDate);
        return vnpayParams;
    }

    private static void encodeAndBuildQueryUrl(HashMap<String, String> vnpayParams, ArrayList<String> fieldNames, StringBuilder hashData, StringBuilder query) {
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnpayParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }
    }
}
