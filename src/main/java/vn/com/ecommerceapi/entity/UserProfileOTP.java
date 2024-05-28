package vn.com.ecommerceapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile_otp")
public class UserProfileOTP extends BaseEntity {

    private String username;

    private Boolean status;

    private String otp;

    private String type;

    @Column(name = "verify_at")
    private LocalDateTime verifyAt;

    @Column(name = "count_verify_false")
    private Integer countVerifyFalse;

    @Column(name = "last_verify_at")
    private LocalDateTime lastVerifyAt;

}