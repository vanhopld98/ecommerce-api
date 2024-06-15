package vn.com.ecommerceapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String imageUrl;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private CategoryResponse category;
}
