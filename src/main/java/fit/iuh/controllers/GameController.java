package fit.iuh.controllers;

import fit.iuh.dtos.GameDTO;
import fit.iuh.dtos.GameSearchResponseDTO;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.services.GameBasicInfoService;
import fit.iuh.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    private final GameBasicInfoService gameBasicInfoService;

    @GetMapping
    public ResponseEntity<List<GameDTO>> getGames(
            @RequestParam(required = false) String categoryName) {
        List<GameDTO> games = gameService.findGamesByCategoryName(categoryName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/top")
    public ResponseEntity<List<GameDTO>> getTopRatedGames(
            @RequestParam(defaultValue = "5") int limit) {
        List<GameDTO> games = gameService.findTopRatedGames(limit);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/favorites/{customerId}")
    public List<GameBasicInfo> getFavoriteGames(@PathVariable Long customerId) {
        return gameBasicInfoService.findAllByGameFavoriteWithCustomerId(customerId);
    }

    @GetMapping("/search")
    // THAY ĐỔI KIỂU TRẢ VỀ Ở ĐÂY
    public ResponseEntity<Page<GameSearchResponseDTO>> searchGames(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 20) Pageable pageable
    ) {

        // Không cần thay đổi gì ở đây
        Page<GameSearchResponseDTO> games = gameService.searchAndFilterGames(
                keyword, categoryId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(games);
    }
}