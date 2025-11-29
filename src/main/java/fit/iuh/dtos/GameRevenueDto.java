package fit.iuh.dtos;

import java.math.BigDecimal;

public record GameRevenueDto(
        Long gameId,
        String gameTitle,
        String gameThumbnail,
        Long totalOrders,
        BigDecimal totalRevenue
) {}