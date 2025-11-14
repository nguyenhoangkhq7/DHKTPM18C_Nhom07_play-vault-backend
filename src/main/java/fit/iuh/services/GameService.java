package fit.iuh.services;

import fit.iuh.dtos.GameDTO;
import fit.iuh.dtos.GameSearchResponseDTO;
import fit.iuh.models.Game;
import fit.iuh.repositories.GameRepository;
import fit.iuh.specifications.GameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Game:
 * - Tìm kiếm nâng cao (filter, search, pagination)
 * - Lấy danh sách game theo nhiều tiêu chí
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;


    // ========================================================================
    // 1. TÌM KIẾM & LỌC GAME NÂNG CAO (main) → Dùng Specification + Pagination
    // ========================================================================
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDTO> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        BigDecimal minPriceBd = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal maxPriceBd = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

        Specification<Game> spec = GameSpecification.filterBy(keyword, categoryId, minPriceBd, maxPriceBd);
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        return gamePage.map(GameSearchResponseDTO::fromEntity);
    }


    // ========================================================================
    // 2. CÁC PHƯƠNG THỨC CƠ BẢN (phucvinh) → Dành cho API đơn giản
    // ========================================================================
    @Transactional(readOnly = true)
    public List<GameDTO> findAll() {
        return gameRepository.findAll().stream()
                .map(GameDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public GameDTO findById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
        return GameDTO.fromEntity(game);
    }

    @Transactional(readOnly = true)
    public List<GameDTO> findGamesByCategoryName(String categoryName) {
        return gameRepository.findByGameBasicInfos_Category_Name(categoryName).stream()
                .map(GameDTO::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GameDTO> findTopRatedGames(int topN) {
        return gameRepository.findTopRatedGames(topN).stream()
                .map(GameDTO::fromEntity)
                .toList();
    }
}