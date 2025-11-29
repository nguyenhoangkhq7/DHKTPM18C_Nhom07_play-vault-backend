package fit.iuh.controllers;

import fit.iuh.dtos.DashboardStatsResponse;
import fit.iuh.dtos.GameDetailDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/games")
@RequiredArgsConstructor
public class AdminGameController {

    private final GameService gameService;

    @GetMapping("/approved")
    public ResponseEntity<Page<GameSearchResponseDto>> getApprovedGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false, defaultValue = "all") String categoryFilter
    ) {
        String effectiveCategory = "all".equalsIgnoreCase(categoryFilter) ? null : categoryFilter;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<GameSearchResponseDto> result = gameService.searchAndFilterApprovedGames(pageable, searchQuery, effectiveCategory);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(gameService.getDashboardStats());
    }

    @PutMapping("/{gameId}/status")
    public ResponseEntity<GameDetailDto> updateGameStatus(
            @PathVariable Long gameId,
            @RequestParam String newStatus
    ) {
        GameDetailDto updatedGame = gameService.updateApprovedGameStatus(gameId, newStatus);
        return ResponseEntity.ok(updatedGame);
    }

    // --- SỬA TẠI ĐÂY: Đổi kiểu trả về thành Page ---
    @GetMapping("/pending")
    public ResponseEntity<Page<GameSearchResponseDto>> getPendingGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<GameSearchResponseDto> result = gameService.searchAndFilterPendingGames(pageable, searchQuery);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameDetailDto> getGameDetailForAdmin(@PathVariable Long gameId) {
        GameDetailDto detail = gameService.getGameForAdmin(gameId);
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{gameId}/approve")
    public ResponseEntity<GameDetailDto> approveGame(@PathVariable Long gameId) {
        GameDetailDto approvedGame = gameService.approveGame(gameId);
        return ResponseEntity.ok(approvedGame);
    }

    @PutMapping("/{gameId}/reject")
    public ResponseEntity<GameDetailDto> rejectGame(@PathVariable Long gameId) {
        try {
            GameDetailDto rejectedGame = gameService.rejectGame(gameId);
            return ResponseEntity.ok(rejectedGame);
        } catch (Exception e) {
            return ResponseEntity.ok().build();
        }
    }

}