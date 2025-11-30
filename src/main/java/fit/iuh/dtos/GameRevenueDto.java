package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRevenueDto {
    private Long gameId;
    private String name;
    private BigDecimal revenue;
    private Long sales;
    private String thumbnail; // Map với GameBasicInfo.thumbnail
    private String category;  // Map với Category.name
}