// trong file GameController.java
package fit.iuh.controllers;

import fit.iuh.dtos.GameSearchResponseDTO; // Import DTO
import fit.iuh.services.GameService;
// ...
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
public class GameController {

    @Autowired
    private GameService gameService;

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