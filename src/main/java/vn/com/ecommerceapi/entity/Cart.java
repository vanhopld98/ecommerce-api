package vn.com.ecommerceapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
public class Cart extends BaseEntity {

    private String username;
    private int quantity;
    private String productId;

}
