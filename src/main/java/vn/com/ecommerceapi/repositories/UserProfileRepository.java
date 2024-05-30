package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.UserProfile;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

    Optional<UserProfile> findByUsername(String username);

    @Query(value = "select keycloak_id from user_profile where username = :username", nativeQuery = true)
    String getKeycloakIdByUsername(@Param("username") String username);

    @Query(value = "select * from user_profile order by created_at desc", nativeQuery = true)
    List<UserProfile> findUserProfilesOrderByCreatedAtDesc();
}
