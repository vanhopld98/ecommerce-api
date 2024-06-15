package vn.com.ecommerceapi.mapper;


import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.model.response.ProductResponse;

@Component
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse mapToProductResponse(Product product);

}
