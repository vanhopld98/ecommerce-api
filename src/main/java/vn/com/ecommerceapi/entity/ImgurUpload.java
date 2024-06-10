package vn.com.ecommerceapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "imgur_upload")
public class ImgurUpload extends BaseEntity {

    @Column(name = "status")
    private String status;

    @Column(name = "imgur_id")
    private String imgurId;

    @Column(name = "delete_hash")
    private String deleteHash;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "size")
    private int size;

    @Column(name = "type")
    private String type;

    @Column(name = "imgur_url")
    private String imgurUrl;

}
