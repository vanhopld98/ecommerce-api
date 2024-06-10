package vn.com.ecommerceapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.aop.Secured;
import vn.com.ecommerceapi.enums.RoleEnum;
import vn.com.ecommerceapi.model.request.CreateProductRequest;
import vn.com.ecommerceapi.model.response.ProductResponse;
import vn.com.ecommerceapi.model.response.ProductsResponse;
import vn.com.ecommerceapi.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ProductsResponse> getProducts(@RequestParam("page") int page,
                                                        @RequestParam("size") int size) {
        return ResponseEntity.ok(productService.getProducts(page, size));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

//    @Secured(role = RoleEnum.ADMIN)
    @PostMapping(value = "/product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @ModelAttribute CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PutMapping("/product/quantity")
    public ResponseEntity<ProductResponse> updateQuantityProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

}
