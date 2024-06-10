package vn.com.ecommerceapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
public class Product extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private String categoryId;
    private Integer quantity;
}
