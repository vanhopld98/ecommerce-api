package vn.com.ecommerceapi.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import vn.com.ecommerceapi.entity.Category;
import vn.com.ecommerceapi.model.response.CategoryResponse;

@Component
@Mapper(componentModel = "spring")
public interface CategoriesMapper {

    CategoryResponse mapToCategoryResponse(Category category);

}
