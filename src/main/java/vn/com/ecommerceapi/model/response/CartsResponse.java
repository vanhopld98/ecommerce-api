package vn.com.ecommerceapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartsResponse {

    private List<CartResponse> carts;
    private BigDecimal totalAmount;
    private String address;

}
