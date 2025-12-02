package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.services.PublisherRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/publisher/revenue")
@RequiredArgsConstructor
public class PublisherRevenueController {

    private final PublisherRevenueService revenueService;

    // Lấy Publisher ID từ token – chuẩn như PromotionController
    private Long getCurrentPublisherId(Authentication authentication) {
        String username = authentication.getName(); // username đang login (ví dụ: publisher1)
        return revenueService.getPublisherIdByUsername(username);
    }

    @GetMapping("/summary")
    public ResponseEntity<RevenueSummaryDto> getSummary(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Long publisherId = getCurrentPublisherId(authentication);
        return ResponseEntity.ok(revenueService.getSummary(publisherId, from, to));
    }

    @GetMapping("/by-game")
    public ResponseEntity<List<GameRevenueDto>> getRevenueByGame(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        Long publisherId = getCurrentPublisherId(authentication);
        return ResponseEntity.ok(revenueService.getRevenueByGame(publisherId, from, to));
    }
    @GetMapping("/by-game/{gameId}/monthly")
    public ResponseEntity<List<MonthlyRevenueDto>> getGameMonthlyRevenue(
            Authentication authentication,
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "2025") int year) {

        Long publisherId = getCurrentPublisherId(authentication);
        return ResponseEntity.ok(revenueService.getGameMonthlyRevenue(publisherId, gameId, year));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlyRevenueDto>> getMonthlyRevenue(
            Authentication authentication,
            @RequestParam(defaultValue = "2025") int year) {

        Long publisherId = getCurrentPublisherId(authentication);
        return ResponseEntity.ok(revenueService.getMonthlyRevenue(publisherId, year));
    }


}