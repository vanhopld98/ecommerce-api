package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.ecommerceapi.entity.UserProfileOTP;

@Repository
public interface UserProfileOTPRepository extends JpaRepository<UserProfileOTP, String> {

    @Query(value = "select * from user_profile_otp where username =:username and type =:type order by created_at desc limit 1 ", nativeQuery = true)
    UserProfileOTP getLatestOTP(String username, String type);

    @Transactional
    @Modifying
    @Query(value = " update user_profile_otp set status = false where username =:username and type =:type ", nativeQuery = true)
    void inactiveAllStatus(String username, String type);
}
