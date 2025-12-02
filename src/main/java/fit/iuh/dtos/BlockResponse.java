package fit.iuh.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BlockResponse(
        Long blockId,
        String username,
        String reason,
        LocalDate blockedAt,
//        LocalDateTime unblockedAt,
        boolean isCurrentlyBlocked,
        String action,           // "BLOCKED" hoặc "UNBLOCKED"
        String message
) {}