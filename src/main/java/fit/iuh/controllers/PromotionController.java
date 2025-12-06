package fit.iuh.controllers;

import fit.iuh.dtos.ApplyPromotionDto;
import fit.iuh.dtos.GameSimpleDto;
import fit.iuh.dtos.PromotionRequestDto;
import fit.iuh.dtos.PromotionResponseDto;
import fit.iuh.services.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // 1. Lấy danh sách (giữ để tương thích cũ)
    @GetMapping
    public ResponseEntity<List<PromotionResponseDto>> getMyPromotions(Authentication authentication) {
        return ResponseEntity.ok(promotionService.getMyPromotions(authentication.getName()));
    }

    // 2. Tạo khuyến mãi
    @PostMapping
    public ResponseEntity<PromotionResponseDto> createPromotion(
            Authentication authentication,
            @Valid @RequestBody PromotionRequestDto request) {
        return ResponseEntity.ok(promotionService.createPromotion(authentication.getName(), request));
    }

    // 3. Áp dụng khuyến mãi cho game
    @PostMapping("/{id}/apply")
    public ResponseEntity<String> applyPromotion(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ApplyPromotionDto applyDto) {

        promotionService.applyPromotionToGames(id, authentication.getName(), applyDto.getGameIds());
        return ResponseEntity.ok("Đã áp dụng khuyến mãi thành công cho các game được chọn");
    }

    // 4. Gỡ khuyến mãi
    @DeleteMapping("/games/{gameId}")
    public ResponseEntity<String> removePromotionFromGame(
            Authentication authentication,
            @PathVariable Long gameId) {
        promotionService.removePromotionFromGame(gameId, authentication.getName());
        return ResponseEntity.ok("Đã gỡ khuyến mãi khỏi game");
    }

    // 5. TÌM KIẾM + LỌC NÂNG CAO + PHÂN TRANG (HOÀN HẢO CHO GIAO DIỆN CỦA BẠN)
    @GetMapping("/search")
    public ResponseEntity<Page<PromotionResponseDto>> searchPromotions(
            Authentication authentication,

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false, defaultValue = "ALL") String status, // ALL, ACTIVE, UPCOMING, EXPIRED
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PromotionResponseDto> result = promotionService.searchMyPromotions(
                authentication.getName(), keyword, fromDate, toDate,
                status == null ? "ALL" : status, page, size);

        return ResponseEntity.ok(result);
    }
    // 6. LẤY DANH SÁCH GAME CỦA CHÍNH PUBLISHER ĐÓ (có ảnh, giá, tên, rating)
    @GetMapping("/my-games")
    public ResponseEntity<List<GameSimpleDto>> getMyGames(Authentication authentication) {
        List<GameSimpleDto> myGames = promotionService.getMyGames(authentication.getName());
        return ResponseEntity.ok(myGames);
    }
    // Thêm vào PromotionController.java

    // 7. SỬA KHUYẾN MÃI
    @PutMapping("/{id}")
    public ResponseEntity<PromotionResponseDto> updatePromotion(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody PromotionRequestDto request) {

        PromotionResponseDto updated = promotionService.updatePromotion(
                authentication.getName(), id, request);

        return ResponseEntity.ok(updated);
    }
    // 6. LẤY DANH SÁCH GAME CỦA CHÍNH PUBLISHER ĐÓ (QUAN TRỌNG NHẤT!)

}