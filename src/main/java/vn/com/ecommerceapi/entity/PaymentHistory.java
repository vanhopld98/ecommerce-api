package vn.com.ecommerceapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_history")
@EqualsAndHashCode(callSuper = false)
public class PaymentHistory extends BaseEntity {

    private String username;

}
