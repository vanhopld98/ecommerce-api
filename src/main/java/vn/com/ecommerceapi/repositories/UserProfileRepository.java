package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.UserProfile;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findByUsername(String username);

}
