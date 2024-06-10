package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    boolean existsByName(String name);

    @Query(value = "select * from category where is_active = true order by name", nativeQuery = true)
    List<Category> getAllCategoryActive();
}
