package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.Cart;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.mapper.CartMapper;
import vn.com.ecommerceapi.model.request.AddCartRequest;
import vn.com.ecommerceapi.model.request.UpdateCartRequest;
import vn.com.ecommerceapi.model.response.CartResponse;
import vn.com.ecommerceapi.model.response.CartsResponse;
import vn.com.ecommerceapi.repositories.CartRepository;
import vn.com.ecommerceapi.repositories.ProductRepository;
import vn.com.ecommerceapi.repositories.UserProfileRepository;
import vn.com.ecommerceapi.service.CartService;
import vn.com.ecommerceapi.utils.JWTUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggingFactory.getLogger(CartServiceImpl.class);

    private static final List<String> TYPES = List.of("ADD", "DECREASE");
    private static final String ADD = "ADD";

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final UserProfileRepository userProfileRepository;

    @Override
    public CartsResponse getCart() {
        String username = JWTUtils.getUsername();

        List<Cart> carts = cartRepository.findAllByUsernameOrderByCreatedAtDesc(username);
        LOGGER.info("[CART][GET CARTS][{}] Carts: {}", username, carts);

        List<CartResponse> cartsResponse = new ArrayList<>();

        for (Cart cart : carts) {
            Product product = productRepository.findById(cart.getProductId()).orElse(null);
            if (product != null) {
                CartResponse cartResponse = cartMapper.map(product, cart);
                cartResponse.setTotalPrice(new BigDecimal(cart.getQuantity()).multiply(product.getPrice()));
                cartsResponse.add(cartResponse);
            }
        }

        List<BigDecimal> prices = cartsResponse.stream().map(CartResponse::getTotalPrice).toList();

        BigDecimal totalPrice = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        LOGGER.info("[CART][GET CARTS][{}] Tổng số tiền của đơn hàng: {}", username, totalPrice);

        String address = userProfileRepository.getAddressByUsername(username);
        LOGGER.info("[CART][GET CARTS][{}] Địa chỉ của đơn hàng: {}", username, address);

        return new CartsResponse(cartsResponse, totalPrice, address);
    }

    @Override
    public void addCart(AddCartRequest request) {
        String username = JWTUtils.getUsername();
        Cart cart = cartRepository.findByUsernameAndProductId(username, request.getProductId()).orElse(null);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
        } else {
            cart = new Cart();
            cart.setProductId(request.getProductId());
            cart.setUsername(username);
            cart.setQuantity(1);
        }

        cartRepository.save(cart);
    }

    @Override
    public void removeCart(String id) {
        String username = JWTUtils.getUsername();
        Cart cart = cartRepository.findByUsernameAndProductId(username, id).orElse(null);
        if (cart == null) {
            throw new BusinessException("Không tìm thấy thông tin sản phẩm");
        }
        cartRepository.delete(cart);
    }

    @Override
    public void updateCart(UpdateCartRequest request, String productId) {
        String username = JWTUtils.getUsername();

        if (!TYPES.contains(request.getType())) {
            throw new BusinessException("Loại cập nhật sản phẩm không hợp lệ");
        }

        Cart cart = cartRepository.findByUsernameAndProductId(username, productId).orElse(null);
        if (cart == null) {
            throw new BusinessException("Không tìm thấy thông tin sản phẩm");
        }

        if (StringUtils.equals(request.getType(), ADD)) {
            cart.setQuantity(cart.getQuantity() + 1);
            cartRepository.save(cart);
        } else {
            if (cart.getQuantity() > 0) {
                cart.setQuantity(cart.getQuantity() - 1);
                cartRepository.save(cart);
            }
            if (cart.getQuantity() == 0) {
                cartRepository.delete(cart);
            }
        }

    }

}
