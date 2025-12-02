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
            @RequestParam(defaultValue = "all") String categoryFilter,
            @RequestParam(defaultValue = "default") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<GameSearchResponseDto> result = gameService.searchAndFilterApprovedGames(
                pageable,
                searchQuery,
                categoryFilter,
                sortBy
        );

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
    public ResponseEntity<Void> rejectGame(
            @PathVariable Long gameId,
            @RequestParam(defaultValue = "Không đạt yêu cầu") String reason // Thêm lý do
    ) {
        gameService.rejectGame(gameId, reason);
        return ResponseEntity.ok().build();
    }

    // API này thay thế cho /pending cũ, hỗ trợ lấy ALL, APPROVED, REJECTED
    @GetMapping("/submissions")
    public ResponseEntity<Page<GameSearchResponseDto>> getGameSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "all") String status // Param lọc trạng thái
    ) {
        Pageable pageable = PageRequest.of(page, size); // Sort đã được xử lý trong Query
        Page<GameSearchResponseDto> result = gameService.searchGameSubmissions(pageable, searchQuery, status);
        return ResponseEntity.ok(result);
    }
}