package fit.iuh.dtos;

import java.math.BigDecimal;

public record GameRevenueDto(
        Long gameId,
        String gameName,
        BigDecimal revenue,
        Long playerCount
) {}