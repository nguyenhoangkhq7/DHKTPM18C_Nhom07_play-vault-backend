package fit.iuh.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequestDto {
    // Frontend sẽ gửi một JSON body chứa trường "itemIds"
    @NotNull(message = "Danh sách ID sản phẩm không được để trống")
    @NotEmpty(message = "Danh sách ID sản phẩm không được để trống")
    private List<Long> itemIds;
}