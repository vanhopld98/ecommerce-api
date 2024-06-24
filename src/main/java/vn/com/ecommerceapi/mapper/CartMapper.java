package vn.com.ecommerceapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import vn.com.ecommerceapi.entity.Cart;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.model.response.CartResponse;

@Component
@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "quantity", source = "cart.quantity")
    CartResponse map(Product product, Cart cart);
}
