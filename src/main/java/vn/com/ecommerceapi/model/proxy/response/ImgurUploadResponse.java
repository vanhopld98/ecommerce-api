package vn.com.ecommerceapi.model.proxy.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImgurUploadResponse {

    private float status;
    private boolean success;
    private ImgurUploadData data;

}

