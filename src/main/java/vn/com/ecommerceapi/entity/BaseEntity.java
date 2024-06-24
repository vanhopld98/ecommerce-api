package vn.com.ecommerceapi.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "created_at")
    @CreationTimestamp
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime updatedAt;

}
