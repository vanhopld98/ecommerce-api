package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.entity.Category;
import vn.com.ecommerceapi.exception.BusinessException;
import vn.com.ecommerceapi.mapper.CategoriesMapper;
import vn.com.ecommerceapi.model.request.CreateCategoryRequest;
import vn.com.ecommerceapi.model.request.UpdateCategoryRequest;
import vn.com.ecommerceapi.model.response.CategoriesResponse;
import vn.com.ecommerceapi.model.response.CategoryResponse;
import vn.com.ecommerceapi.repositories.CategoryRepository;
import vn.com.ecommerceapi.service.CategoryService;
import vn.com.ecommerceapi.utils.JWTUtils;
import vn.com.ecommerceapi.utils.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoriesMapper categoriesMapper;

    @Override
    public CategoriesResponse findAll() {
        List<Category> categories = categoryRepository.getAllCategoryActive();
        List<CategoryResponse> categoriesResponses = categories.stream().map(categoriesMapper::mapToCategoryResponse).toList();
        return CategoriesResponse.builder().categories(categoriesResponses).build();
    }

    @Override
    public void create(CreateCategoryRequest request) {
        String username = JWTUtils.getUsername();
        if (StringUtils.isNullOrEmpty(request.getName())) {
            throw new BusinessException("Tên loại sản phẩm không được để trống!");
        }
        Boolean isExist = categoryRepository.existsByName(request.getName());
        if (Boolean.TRUE.equals(isExist)) {
            throw new BusinessException("Loại sản phẩm này đã tồn tại trong hệ thống!");
        }
        Category category = new Category(request.getName(), true);
        categoryRepository.save(category);
        LOGGER.info("[CATEGORY][{}][CREATE] Đã tạo mới loại sản phẩm: {}", username, request.getName());
    }

    @Override
    public CategoryResponse update(String id, UpdateCategoryRequest request) {
        String username = JWTUtils.getUsername();
        LOGGER.info("[CATEGORY][UPDATE][{}] Starting... Request: {}", username, request);

        validateRequest(id, request.getName());

        Category category = categoryRepository.findById(id).orElse(null);
        LOGGER.info("[CATEGORY][UPDATE][{}] Category Old: {}", username, category);

        if (category == null) {
            throw new BusinessException("Không tìm thấy thông tin loại sản phẩm");
        }

        updateCategory(request, category);
        LOGGER.info("[CATEGORY][UPDATE][{}] Category New: {}", username, category);

        return categoriesMapper.mapToCategoryResponse(category);
    }

    private void updateCategory(UpdateCategoryRequest request, Category category) {
        category.setActive(request.isActive());
        category.setName(request.getName());
        categoryRepository.save(category);
    }

    private static void validateRequest(String id, String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            throw new BusinessException("Tên loại sản phẩm không được để trống.");
        }
        if (StringUtils.isNullOrEmpty(id)) {
            throw new BusinessException("ID loại sản phẩm không được để trống.");
        }
    }

}
