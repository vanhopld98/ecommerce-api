package vn.com.ecommerceapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private String productId;
    private String productName;
    private int price;
    private int quantity;
    private String imageUrl;
    private BigDecimal totalPrice;

}
