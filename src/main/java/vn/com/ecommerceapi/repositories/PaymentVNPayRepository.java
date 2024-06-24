package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.PaymentVNPay;

@Repository
public interface PaymentVNPayRepository extends JpaRepository<PaymentVNPay, String> {

}
