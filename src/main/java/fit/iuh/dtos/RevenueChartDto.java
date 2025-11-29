package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueChartDto {
    private Integer month;
    private Double revenue;
}