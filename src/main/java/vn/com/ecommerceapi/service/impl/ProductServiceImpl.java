package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.model.request.CreateProductRequest;
import vn.com.ecommerceapi.model.response.ProductResponse;
import vn.com.ecommerceapi.model.response.ProductsResponse;
import vn.com.ecommerceapi.repositories.CategoryRepository;
import vn.com.ecommerceapi.service.ProductService;
import vn.com.ecommerceapi.utils.JWTUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;

    @Override
    public ProductsResponse getProducts(int page, int size) {
        return null;
    }

    @Override
    public ProductResponse getProduct(String id) {
        return null;
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        String username = JWTUtils.getUsername();

        /* Lấy thông tin category xem có tồn tại không */
        boolean isCategoryExist = categoryRepository.existsById(request.getCategoryId());
        if (!isCategoryExist) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }
        return null;
    }
}
