// trong file GameService.java
package fit.iuh.services;

import fit.iuh.dtos.GameSearchResponseDTO; // Import DTO
import fit.iuh.models.Game;
// ... các import khác
import fit.iuh.repositories.GameRepository;
import fit.iuh.specifications.GameSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Thêm import này

import java.math.BigDecimal;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    // Thêm @Transactional(readOnly = true)
    // Điều này giữ session mở trong suốt quá trình map DTO
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDTO> searchAndFilterGames(
            String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {

        BigDecimal minPriceBd = (minPrice != null) ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal maxPriceBd = (maxPrice != null) ? BigDecimal.valueOf(maxPrice) : null;

        Specification<Game> spec = GameSpecification.filterBy(
                keyword, categoryId, minPriceBd, maxPriceBd);

        // 1. Lấy Page<Game> như cũ
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        // 2. Chuyển đổi (map) nó sang Page<GameSearchResponseDTO>
        // Hàm .map() của Page sẽ gọi hàm GameSearchResponseDTO.fromEntity
        Page<GameSearchResponseDTO> dtoPage = gamePage.map(GameSearchResponseDTO::fromEntity);

        return dtoPage;
    }
}