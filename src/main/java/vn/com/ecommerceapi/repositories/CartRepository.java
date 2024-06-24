package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.Cart;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    List<Cart> findAllByUsernameOrderByCreatedAtDesc(String username);

    Optional<Cart> findByUsernameAndProductId(String username, String productId);

}
