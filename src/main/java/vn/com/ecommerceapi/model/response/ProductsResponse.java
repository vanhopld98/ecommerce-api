package vn.com.ecommerceapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductsResponse {

    private int totalElements;
    private List<ProductResponse> productResponses;

}
