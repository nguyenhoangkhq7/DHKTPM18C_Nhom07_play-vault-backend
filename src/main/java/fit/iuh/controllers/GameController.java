package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.services.GameBasicInfoService;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.dtos.ReviewDto;
import fit.iuh.services.GameService;
import fit.iuh.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Thêm import Authentication
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames(
            @RequestParam(required = false) String categoryName) {
        List<GameDto> games = gameService.findGamesByCategoryName(categoryName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/top")
    public ResponseEntity<List<GameWithRatingDto>> getTopRatedGames(
            @RequestParam(defaultValue = "0") int limit) {
        List<GameWithRatingDto> gameWithRatingDtos= gameService.getTopGamesWithRating(limit);
        return ResponseEntity.ok(gameWithRatingDtos);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<GameSearchResponseDto>> searchGames(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 12) Pageable pageable // Mặc định 12 game/trang
    ) {
        Page<GameSearchResponseDto> games = gameService.searchAndFilterGames(
                keyword, categoryId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(games);
    }

    // ========================================================================
    // SỬA: Lấy chi tiết Game, kiểm tra quyền sở hữu (Buy & Download Logic)
    // ========================================================================
//    @GetMapping("/{id}")
//    public ResponseEntity<fit.iuh.dtos.GameDetailDto> getGameDetail(
//            @PathVariable Long id,
//            Authentication authentication // Inject thông tin user đang đăng nhập
//    ) {
//        fit.iuh.models.Game game = gameService.findGameEntityById(id);
//
//        if (game == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        boolean isOwned = false;
//
//        // 1. Nếu người dùng đã đăng nhập và là Customer, kiểm tra quyền sở hữu
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            // TODO: Bổ sung logic kiểm tra xem user này có phải là Customer không (nếu cần)
//            isOwned = gameService.checkOwnership(username, id);
//        }
//
//        // 2. Trả về DTO, truyền cờ sở hữu vào phương thức fromEntity
//        return ResponseEntity.ok(fit.iuh.dtos.GameDetailDto.fromEntity(game, isOwned));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailDto> getGameDetail(
            @PathVariable Long id,
            Authentication authentication) {

        var gameEntity = gameService.findGameEntityById(id);
        if (gameEntity == null) return ResponseEntity.notFound().build();

        boolean isOwned = false;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            isOwned = gameService.checkOwnership(username, id); // check ownership
        }

        GameDetailDto dto = GameDetailDto.fromEntity(gameEntity, isOwned);
        return ResponseEntity.ok(dto);
    }


    // ========================================================================
    // Giữ nguyên các phương thức còn lại
    // ========================================================================
    @GetMapping("card/{id}")
    public ResponseEntity<GameWithRatingDto> getGameById(@PathVariable Long id) {
        GameWithRatingDto gameDto = gameService.getGameWithRatingById(id);

        if (gameDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameDto);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<List<ReviewDto>> getReviewById(@PathVariable Long id) {
        List<ReviewDto> reviews= reviewService.findReviewsByGame_IdOrderByCreatedAtDesc(id);
        return ResponseEntity.ok(reviews);
    }
}