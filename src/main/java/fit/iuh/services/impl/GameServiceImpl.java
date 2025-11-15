package fit.iuh.services.impl;

import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.Game;
import fit.iuh.repositories.GameRepository;
import fit.iuh.services.GameService;
import fit.iuh.specifications.GameSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper; // MapStruct

    // ========================================================================
    // 1. TÌM KIẾM & LỌC NÂNG CAO (Specification + Pagination)
    // ========================================================================
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDto> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            Pageable pageable) {

        BigDecimal minPriceBd = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal maxPriceBd = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

        Specification<Game> spec = GameSpecification.filterBy(keyword, categoryId, minPriceBd, maxPriceBd);
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        return gamePage.map(GameSearchResponseDto::fromEntity);
        // Hoặc dùng MapStruct: return gamePage.map(gameMapper::toSearchResponseDTO);
    }

    // ========================================================================
    // 2. CÁC PHƯƠNG THỨC CƠ BẢN
    // ========================================================================
    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findAll() {
        return gameRepository.findAll()
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GameDto findById(Long id) {
        return gameRepository.findById(id)
                .map(gameMapper::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findGamesByCategoryName(String categoryName) {
        List<Game> games = (categoryName == null || categoryName.isBlank())
                ? gameRepository.findAll()
                : gameRepository.findByGameBasicInfos_Category_Name(categoryName);

        return games.stream()
                .map(gameMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findTopRatedGames(int topN) {
        return gameRepository.findTopRatedGames(topN)
                .stream()
                .map(gameMapper::toDTO)
                .toList();
    }
}