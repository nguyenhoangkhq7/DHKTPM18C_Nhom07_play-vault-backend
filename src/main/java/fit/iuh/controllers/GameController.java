package fit.iuh.controllers;

import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
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
    public ResponseEntity<List<GameDto>> getGames(
            @RequestParam(required = false) String categoryName) {
        List<GameDto> games = gameService.findGamesByCategoryName(categoryName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/top")
    public ResponseEntity<List<GameDto>> getTopRatedGames(
            @RequestParam(defaultValue = "5") int limit) {
        List<GameDto> games = gameService.findTopRatedGames(limit);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/favorites/{customerId}")
    public List<GameBasicInfo> getFavoriteGames(@PathVariable Long customerId) {
        return gameBasicInfoService.findAllByGameFavoriteWithCustomerId(customerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<GameSearchResponseDto>> searchGames(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @PageableDefault(size = 12) Pageable pageable // Mặc định 12 game/trang
    ) {
        // Gọi hàm searchAndFilterGames mà bạn vừa viết trong Service
        Page<GameSearchResponseDto> games = gameService.searchAndFilterGames(
                keyword, categoryId, minPrice, maxPrice, pageable);

        return ResponseEntity.ok(games);
    }


    @GetMapping("/{id}")
    public ResponseEntity<fit.iuh.dtos.GameDetailDto> getGameDetail(@PathVariable Long id) {
        fit.iuh.models.Game game = gameService.findGameEntityById(id);

        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fit.iuh.dtos.GameDetailDto.fromEntity(game));
    }
}