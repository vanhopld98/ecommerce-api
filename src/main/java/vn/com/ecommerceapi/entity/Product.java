package vn.com.ecommerceapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@EqualsAndHashCode(callSuper = false)
public class Product extends BaseEntity {

    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @Column(name = "quantity")
    private Integer quantity;

}
