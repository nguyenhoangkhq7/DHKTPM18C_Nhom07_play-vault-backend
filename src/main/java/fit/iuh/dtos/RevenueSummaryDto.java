// RevenueSummaryDto.java (fit.iuh.dtos)
package fit.iuh.dtos;

import java.math.BigDecimal;

public record RevenueSummaryDto(
        BigDecimal totalRevenue,
        BigDecimal growthPercent,     // +12.5%
        Long totalPlayers,            // 44,500
        Long totalGames,              // 2
        BigDecimal avgRevenuePerGame  // 195,640đ
) {}