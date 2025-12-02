package fit.iuh.services.impl;

import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.Account;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.models.enums.SubmissionStatus;
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
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    public Page<GameSearchResponseDto> searchAndFilterApprovedGames(Pageable pageable, String searchQuery, String categoryFilter, String sortBy) {

        String category = (categoryFilter == null || categoryFilter.isBlank() || "all".equalsIgnoreCase(categoryFilter)) ? "all" : categoryFilter;
        String search = (searchQuery == null || searchQuery.isBlank()) ? null : searchQuery;

        if ("revenue_desc".equalsIgnoreCase(sortBy)) {
            return gameRepository.findGamesSortedByRevenue(search, category, pageable);
        }
        if ("downloads_desc".equalsIgnoreCase(sortBy)) {
            return gameRepository.findGamesSortedByDownloads(search, category, pageable);
        }

        Specification<Game> spec = GameSpecification.filterApprovedGames(searchQuery, categoryFilter);

        Page<Game> page = gameRepository.findAll(spec, pageable);

        Page<GameSearchResponseDto> dtoPage = page.map(GameSearchResponseDto::fromEntity);


        if (!dtoPage.isEmpty()) {

            List<Long> gameIds = dtoPage.getContent().stream()
                    .map(GameSearchResponseDto::getId)
                    .toList();

            if (!gameIds.isEmpty()) {

                List<Object[]> stats = gameRepository.findStatsForGameIds(gameIds);

                java.util.Map<Long, double[]> statsMap = new java.util.HashMap<>();
                for (Object[] row : stats) {
                    Long gId = (Long) row[0];
                    Long downloads = (Long) row[1];
                    Double revenue = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;

                    statsMap.put(gId, new double[]{downloads.doubleValue(), revenue});
                }

                dtoPage.forEach(dto -> {
                    if (statsMap.containsKey(dto.getId())) {
                        double[] s = statsMap.get(dto.getId());
                        dto.setDownloads((long) s[0]); // Set Lượt tải

                        dto.setRevenue(s[1]);
                    } else {
                        dto.setDownloads(0L);
                        dto.setRevenue(0.0);
                    }
                });
            }
        }

        return dtoPage;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public GameDetailDto getGameForAdmin(Long gameId) { return null; }

    @Override
    @Transactional
    public GameDetailDto approveGame(Long submissionId) {
        GameSubmission submission = gameSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu duyệt"));

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể duyệt game đang PENDING");
        }

        // Cập nhật trạng thái
        Account admin = new Account(); admin.setUsername("admin"); // TODO: Lấy ID thật từ SecurityContext
        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setReviewerUsername(admin);
        submission.setReviewedAt(LocalDate.now());
        GameSubmission savedSubmission = gameSubmissionRepository.save(submission);

        // Tạo Game Mới
        Game newGame = new Game();
        newGame.setReleaseDate(LocalDate.now());
        newGame.setGameBasicInfos(savedSubmission.getGameBasicInfos());
        // Map thêm các field cần thiết nếu có

        return GameDetailDto.fromEntity(gameRepository.save(newGame));
    }

    @Override
    @Transactional
    public void rejectGame(Long submissionId, String reason) { // Sửa thành void và thêm reason
        GameSubmission submission = gameSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu duyệt"));

        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể từ chối game đang PENDING");
        }

        Account admin = new Account(); admin.setUsername("admin");
        submission.setStatus(SubmissionStatus.REJECTED);
        submission.setRejectReason(reason);
        submission.setReviewerUsername(admin);
        submission.setReviewedAt(LocalDate.now());

        gameSubmissionRepository.save(submission);
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

    @Override
    @Transactional(readOnly = true)
    public GameDetailDto getGameForAdmin(Long id) {
        // TRƯỜNG HỢP 1: Tìm trong bảng Game (Game ĐÃ DUYỆT/ĐANG BÁN)
        Optional<Game> gameOpt = gameRepository.findById(id);
        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            GameDetailDto dto = GameDetailDto.fromEntity(game);

            // --- LOGIC MỚI: Truy ngược lại ngày gửi (SubmittedAt) ---
            if (game.getGameBasicInfos() != null) {
                // Tìm submission tương ứng với info của game này
                Optional<GameSubmission> originSubmission = gameSubmissionRepository
                        .findFirstByGameBasicInfos_IdOrderBySubmittedAtDesc(game.getGameBasicInfos().getId());

                if (originSubmission.isPresent()) {
                    // Nếu tìm thấy, set ngày gửi thật sự
                    dto.setSubmittedDate(originSubmission.get().getSubmittedAt().toString());
                } else {
                    // Trường hợp data cũ hoặc lỗi không tìm thấy submission gốc
                    dto.setSubmittedDate("N/A");
                }
            }
            // -------------------------------------------------------

            return dto;
        }

        // TRƯỜNG HỢP 2: Tìm trong bảng GameSubmission (Game CHỜ DUYỆT/TỪ CHỐI)
        Optional<GameSubmission> submissionOpt = gameSubmissionRepository.findById(id);
        if (submissionOpt.isPresent()) {
            return GameDetailDto.fromSubmissionEntity(submissionOpt.get());
        }

        // Không tìm thấy
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin game ID: " + id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GameSearchResponseDto> searchGameSubmissions(Pageable pageable, String searchQuery, String statusFilter) {
        SubmissionStatus status = null;

        // Chỉ convert sang Enum nếu khác "ALL" và không rỗng
        if (statusFilter != null && !statusFilter.isBlank() && !"all".equalsIgnoreCase(statusFilter)) {
            try {
                status = SubmissionStatus.valueOf(statusFilter.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu sai format thì mặc định tìm tất cả hoặc log lỗi
                status = null;
            }
        }

        Page<GameSubmission> page = gameSubmissionRepository.searchSubmissions(searchQuery, status, pageable);

        // Map sang DTO
        return page.map(GameSearchResponseDto::fromSubmissionEntity);
    }
}