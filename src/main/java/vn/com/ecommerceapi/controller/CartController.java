package vn.com.ecommerceapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.aop.Secured;
import vn.com.ecommerceapi.enums.RoleEnum;
import vn.com.ecommerceapi.model.request.AddCartRequest;
import vn.com.ecommerceapi.model.request.UpdateCartRequest;
import vn.com.ecommerceapi.model.response.CartsResponse;
import vn.com.ecommerceapi.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;

    @Secured(role = RoleEnum.USER)
    @GetMapping("/cart")
    public ResponseEntity<CartsResponse> carts() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @Secured(role = RoleEnum.USER)
    @PostMapping("/cart")
    public ResponseEntity<Void> addCart(@RequestBody AddCartRequest request) {
        cartService.addCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Secured(role = RoleEnum.USER)
    @PutMapping("/cart/{productId}")
    public ResponseEntity<Void> updateCart(@Valid @RequestBody UpdateCartRequest request,
                                           @PathVariable String productId) {
        cartService.updateCart(request, productId);
        return ResponseEntity.ok().build();
    }

    @Secured(role = RoleEnum.USER)
    @DeleteMapping("/cart/{productId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("productId") String productId) {
        cartService.removeCart(productId);
        return ResponseEntity.ok().build();
    }

}
