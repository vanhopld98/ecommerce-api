package vn.com.ecommerceapi.repositories.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import vn.com.ecommerceapi.entity.Product;
import vn.com.ecommerceapi.repositories.ProductRepository;
import vn.com.ecommerceapi.utils.StringUtils;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductSpecification {

    public static final String CATEGORY_ID = "categoryId";
    public static final String NAME = "name";

    private final ProductRepository productRepository;

    private static Specification<Product> hasCategoryId(String categoryId) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNullOrEmpty(categoryId)) {
                return null;
            }
            return criteriaBuilder.equal(root.get(CATEGORY_ID), categoryId);
        };
    }

    private static Specification<Product> hasName(String name) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNullOrEmpty(name)) {
                return null;
            }
            return criteriaBuilder.equal(root.get(NAME), name);
        };
    }

    public List<Product> findAllByCategoryIdAndName(String categoryId, String name) {
        Specification<Product> conditions = Specification.where(hasCategoryId(categoryId).and(hasName(name)));
        return productRepository.findAll(conditions);
    }
}
