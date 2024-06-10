package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.ImgurUpload;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.model.proxy.response.ImgurUploadData;
import vn.com.ecommerceapi.model.proxy.response.ImgurUploadResponse;
import vn.com.ecommerceapi.model.request.CreateProductRequest;
import vn.com.ecommerceapi.model.response.ProductResponse;
import vn.com.ecommerceapi.model.response.ProductsResponse;
import vn.com.ecommerceapi.proxy.ImgurProxy;
import vn.com.ecommerceapi.repositories.CategoryRepository;
import vn.com.ecommerceapi.repositories.ImgurUploadRepository;
import vn.com.ecommerceapi.repositories.ProductRepository;
import vn.com.ecommerceapi.service.ProductService;
import vn.com.ecommerceapi.utils.JWTUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ImgurUploadRepository imgurUploadRepository;
    private final CategoryRepository categoryRepository;
    private final ImgurProxy imgurProxy;

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
        LOGGER.info("[PRODUCT][CREATE PRODUCT][{}] Kiểm tra tồn tại loại sản phẩm: {}", username, isCategoryExist);

        if (!isCategoryExist) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }

        /* Uplaod ảnh lên Imgur */
        Map<String, Object> uploadRequest = new HashMap<>();
        uploadRequest.put("image", request.getImage());

        ImgurUploadResponse uploadResponse = imgurProxy.upload(uploadRequest);
        LOGGER.info("[PRODUCT][CREATE PRODUCT][{}] Response Upload Image: {}", username, uploadResponse);

        if (uploadResponse == null || uploadResponse.getData() == null || uploadResponse.isSuccess()) {
            throw new BusinessException("Có lỗi trong quá trình upload ảnh");
        }

        ImgurUploadData imgurData = uploadResponse.getData();
        LOGGER.info("[PRODUCT][CREATE PRODUCT][{}] Data Upload Image: {}", username, imgurData);

        saveImgurUpload(imgurData);

        Product product = saveProduct(request, imgurData);

        return mapToProductResponse(product);
    }

    private static ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setId(product.getId());
        response.setImageUrl(product.getImageUrl());
        return response;
    }

    private Product saveProduct(CreateProductRequest request, ImgurUploadData imgurData) {
        Product product = new Product();
        product.setCategoryId(request.getCategoryId());
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setImageUrl(imgurData.getLink());
        product.setQuantity(request.getQuantity());
        product.setDescription(request.getDescription());
        productRepository.save(product);

        return product;
    }

    private void saveImgurUpload(ImgurUploadData imgurData) {
        ImgurUpload imgurUpload = new ImgurUpload();
        imgurUpload.setImgurId(imgurData.getId());
        imgurUpload.setStatus("UPLOADED");
        imgurUpload.setDeleteHash(imgurData.getDeleteHash());
        imgurUpload.setWidth(imgurData.getWidth());
        imgurUpload.setHeight(imgurData.getHeight());
        imgurUpload.setSize(imgurData.getSize());
        imgurUpload.setType(imgurData.getType());
        imgurUpload.setImgurUrl(imgurData.getLink());
        imgurUploadRepository.save(imgurUpload);
    }
}
