package vn.com.ecommerceapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.ecommerceapi.entity.ImgurUpload;

@Repository
public interface ImgurUploadRepository extends JpaRepository<ImgurUpload, String> {
}
