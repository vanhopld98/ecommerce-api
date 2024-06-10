package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
}
