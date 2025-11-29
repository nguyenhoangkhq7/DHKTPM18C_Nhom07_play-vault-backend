package fit.iuh.services.impl;

import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.Account;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.repositories.GameRepository;
import fit.iuh.repositories.GameSubmissionRepository;
import fit.iuh.services.GameService;
import fit.iuh.specifications.GameSpecification;
import fit.iuh.specifications.GameSubmissionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper; // MapStruct
    private final GameSubmissionRepository gameSubmissionRepository;
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

        // Lấy danh sách Game từ DB
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        // Map sang DTO (Lúc này logic tính toán Rating trong DTO sẽ chạy)
        return gamePage.map(GameSearchResponseDto::fromEntity);
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

    @Override
    @Transactional(readOnly = true)
    public Game findGameEntityById(Long id) {
        // Trả về nguyên con Entity Game lấy từ DB
        return gameRepository.findById(id).orElse(null);
    }
    public List<GameWithRatingDto> getTopGamesWithRating(int topN) {
        List<Game> allGames = gameRepository.findAll();
        if(topN==0){
            return gameMapper.toGameWithRatingDtoList(allGames)
                    .stream()
                    .sorted(Comparator.comparing(GameWithRatingDto::getAvgRating).reversed())
                    .collect(Collectors.toList());
        }
        return gameMapper.toGameWithRatingDtoList(allGames)
                .stream()
                .sorted(Comparator.comparing(GameWithRatingDto::getAvgRating).reversed())
                .limit(topN)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true)
    public GameWithRatingDto getGameWithRatingById(Long id) {
        // 1. Tìm Game bằng ID. Sử dụng findById và orElse(null) để xử lý trường hợp không tìm thấy.
        Game game = gameRepository.findById(id).orElse(null);

        if (game == null) {
            return null;
        }
        return gameMapper.toGameWithRatingDto(game);
    }

    // --- ADMIN: SỬA LẠI TRẢ VỀ PAGE ---
    @Override
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDto> searchAndFilterPendingGames(Pageable pageable, String searchQuery) {
        Specification<GameSubmission> spec = GameSubmissionSpecification.filterPendingSubmissions(searchQuery);
        Page<GameSubmission> page = gameSubmissionRepository.findAll(spec, pageable);
        // Tự động map từng phần tử
        return page.map(GameSearchResponseDto::fromSubmissionEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDto> searchAndFilterApprovedGames(Pageable pageable, String searchQuery, String categoryFilter) {
        Specification<Game> spec = GameSpecification.filterApprovedGames(searchQuery, categoryFilter);
        Page<Game> page = gameRepository.findAll(spec, pageable);
        return page.map(GameSearchResponseDto::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public GameDetailDto getGameForAdmin(Long gameId) { return null; }

    @Override
    @Transactional
    public GameDetailDto approveGame(Long gameId) {
        GameSubmission submission = gameSubmissionRepository.findById(gameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Account admin = new Account(); admin.setUsername("admin_reviewer");
        submission.approve(admin);
        GameSubmission approved = gameSubmissionRepository.save(submission);
        Game newGame = approved.toGameEntity();
        return GameDetailDto.fromEntity(gameRepository.save(newGame));
    }

    @Override
    @Transactional
    public GameDetailDto rejectGame(Long gameId) {
        GameSubmission submission = gameSubmissionRepository.findById(gameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Account admin = new Account(); admin.setUsername("admin_reviewer");
        submission.reject(admin, "Nội dung không đạt.");
        gameSubmissionRepository.save(submission);
        throw new ResponseStatusException(HttpStatus.OK, "Đã từ chối.");
    }

    @Override
    @Transactional
    public GameDetailDto updateApprovedGameStatus(Long gameId, String newStatus) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!newStatus.equalsIgnoreCase("ACTIVE") && !newStatus.equalsIgnoreCase("INACTIVE")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return GameDetailDto.fromEntity(gameRepository.saveAndFlush(game));
    }

    @Override
    public List<GameDto> getGamesByPublisherId(Long publisherId) {

        List<Game> games = gameRepository.findByGameBasicInfos_Publisher_Id(publisherId);

        return games.stream()
                .map(gameMapper::toDTO) // Đảm bảo gameMapper.toDto đã sửa ở bước trước
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        // Các hàm này phải khớp với tên trong GameRepository đã sửa ở bước trước
        long totalGames = gameRepository.countTotalGames();
        long totalDownloads = gameRepository.countTotalDownloads();
        double totalRevenue = gameRepository.sumTotalRevenue();

        return new DashboardStatsResponse(totalGames, totalDownloads, totalRevenue);
    }
}