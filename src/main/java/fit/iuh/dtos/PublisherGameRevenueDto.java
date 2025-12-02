package fit.iuh.dtos;

import java.math.BigDecimal;

public record PublisherGameRevenueDto(
        Long gameId,
        String gameTitle,
        String gameThumbnail,
        Long totalOrders,
        BigDecimal totalRevenue
) {}