package fit.iuh.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ReportSummaryDto {
    // 1. Tổng doanh thu
    private BigDecimal totalRevenue;
    private Double revenueGrowth; 

    // 2. Người dùng mới
    private Long newUsers;
    private Double userGrowth;

    // 3. Game đã bán (Số lượng sản phẩm bán ra)
    private Long soldGames;
    private Double soldGamesGrowth;

    // 4. Tổng đơn hàng
    private Long totalOrders;
    private Double orderGrowth;
}