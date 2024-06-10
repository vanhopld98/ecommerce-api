package vn.com.ecommerceapi.service;

import vn.com.ecommerceapi.model.request.CreateCategoryRequest;
import vn.com.ecommerceapi.model.request.UpdateCategoryRequest;
import vn.com.ecommerceapi.model.response.CategoriesResponse;
import vn.com.ecommerceapi.model.response.CategoryResponse;

public interface CategoryService {

    CategoriesResponse findAll();

    void create(CreateCategoryRequest request);

    CategoryResponse update(String id, UpdateCategoryRequest request);
}
