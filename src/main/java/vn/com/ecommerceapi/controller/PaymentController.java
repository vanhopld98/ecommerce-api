package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.model.response.VNPayInitOrderResponse;
import vn.com.ecommerceapi.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger LOGGER = LoggingFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @PostMapping("/vnpay/init")
    public ResponseEntity<VNPayInitOrderResponse> initOrder() {
        return ResponseEntity.ok(paymentService.initOrder());
    }

}
