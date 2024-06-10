package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.request.CreateProductRequest;
import vn.com.ecommerceapi.model.response.ProductResponse;
import vn.com.ecommerceapi.model.response.ProductsResponse;

public interface ProductService {

    ProductsResponse getProducts(int page, int size);

    ProductResponse getProduct(String id);

    ProductResponse createProduct(CreateProductRequest request);
}
