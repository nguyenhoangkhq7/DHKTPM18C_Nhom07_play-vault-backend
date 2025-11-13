package fit.iuh.controllers;

import fit.iuh.dtos.GameCardDto;
import fit.iuh.models.Game;
import fit.iuh.services.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/library")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @GetMapping("/my-games")
    public ResponseEntity<List<GameCardDto>> getMyPurchasedGames(
            Authentication authentication,
            @RequestParam(required = false) Long categoryId,      // Bộ lọc 1: Thể loại
            @RequestParam(required = false) String priceRange,    // Bộ lọc 2: Mức giá
            @RequestParam(required = false) String status         // Bộ lọc 3: Trạng thái
    ) {
        // 1. Xác thực người dùng
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();

        // 2. Xử lý bộ lọc giá (chuyển "10-50" thành min/max)
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] prices = priceRange.split("-");
            if (prices.length == 2) {
                try {
                    minPrice = new BigDecimal(prices[0]);
                    maxPrice = new BigDecimal(prices[1]);
                } catch (NumberFormatException e) {
                    // Bỏ qua nếu định dạng giá sai
                }
            }
        }

        // 3. Gọi Service với đầy đủ 3 bộ lọc
        List<Game> games = libraryService.getPurchasedGames(
                username,
                categoryId,
                minPrice,
                maxPrice,
                status
        );

        // 4. Chuyển đổi sang DTO để trả về
        List<GameCardDto> gameDtos = games.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        // 5. Trả về kết quả (nếu rỗng sẽ là [], frontend sẽ hiển thị
        // "Không tìm thấy sản phẩm")
        return ResponseEntity.ok(gameDtos);
    }

    /**
     * Phương thức trợ giúp để chuyển đổi Game Entity sang GameCard DTO.
     */
    private GameCardDto convertToDto(Game game) {
        return new GameCardDto(
                game.getId(),
                game.getGameBasicInfos().getName(),
                game.getGameBasicInfos().getThumbnail(),
                game.getGameBasicInfos().getPrice()
        );
    }
}