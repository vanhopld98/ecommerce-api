package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query(value = "select count(*) from product where category_id = :categoryId", nativeQuery = true)
    int getCountByCategoryId(String categoryId);

}
