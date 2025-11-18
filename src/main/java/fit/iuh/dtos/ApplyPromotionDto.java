package fit.iuh.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ApplyPromotionDto {
    // Danh sách ID các game muốn áp dụng khuyến mãi này
    private List<Long> gameIds;
}