package vn.com.ecommerceapi.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.ecommerceapi.aop.Secured;
import vn.com.ecommerceapi.enums.RoleEnum;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.model.request.CreateCategoryRequest;
import vn.com.ecommerceapi.model.request.UpdateCategoryRequest;
import vn.com.ecommerceapi.model.response.CategoriesResponse;
import vn.com.ecommerceapi.model.response.CategoryResponse;
import vn.com.ecommerceapi.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private static final Logger LOGGER = LoggingFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<CategoriesResponse> findAll(@RequestParam(value = "productCount", required = false) boolean productCount) {
        return ResponseEntity.ok(categoryService.findAll(productCount));
    }

    @Secured(role = RoleEnum.ADMIN)
    @PostMapping("/category")
    public ResponseEntity<Void> create(@RequestBody CreateCategoryRequest request) {
        LOGGER.info("[CATEGORY][CREATE] Request:{}", request);
        categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/category/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable("id") String id,
                                                   @RequestBody UpdateCategoryRequest request) {
        LOGGER.info("[CATEGORY][UPDATE] Request:{}", request);
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") String categoryId) {
        LOGGER.info("[CATEGORY][DELETE] ID:{}", categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
