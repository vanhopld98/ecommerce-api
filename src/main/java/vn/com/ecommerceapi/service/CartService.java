package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.request.AddCartRequest;
import vn.com.ecommerceapi.model.request.UpdateCartRequest;
import vn.com.ecommerceapi.model.response.CartsResponse;

public interface CartService {

    CartsResponse getCart();

    void addCart(AddCartRequest request);

    void removeCart(String id);

    void updateCart(UpdateCartRequest request, String productId);
}
