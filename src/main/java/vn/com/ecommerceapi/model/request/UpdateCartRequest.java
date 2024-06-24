package vn.com.ecommerceapi.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {

    @NotNull(message = "Loại cập nhật không được để trống")
    @NotEmpty(message = "Loại cập nhật không được để trống")
    private String type;

}
