package fit.iuh.services.impl;

import fit.iuh.dtos.GameCreateRequest;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.*;
import fit.iuh.models.enums.SubmissionStatus;
import fit.iuh.repositories.*;
import fit.iuh.models.Account;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.models.enums.SubmissionStatus;
import fit.iuh.repositories.CustomerRepository;
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

    // Dependency từ nhánh vanhau (Xử lý duyệt game)
    private final GameSubmissionRepository gameSubmissionRepository;

    // Dependency từ nhánh main (Xử lý thông tin khách hàng/sở hữu)
    private final CustomerRepository customerRepository;

    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformRepository platformRepository;
    // Nếu bạn lưu GameBasicInfo qua cascade từ Game thì KHÔNG cần repo này.
    // Nếu bạn lưu riêng, hãy khai báo:
    private final GameBasicInfoRepository gameBasicInfoRepository;
    // ========================================================================
    // 1. TÌM KIẾM & LỌC NÂNG CAO (Specification + Pagination)
    // ========================================================================
    @Override
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
    // 2. CÁC PHƯƠNG THỨC CƠ BẢN (COMMON)
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

    @Override
    public List<GameWithRatingDto> getTopGamesWithRating(int topN) {
        List<Game> allGames = gameRepository.findAll();
        if(topN == 0){
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
        Game game = gameRepository.findById(id).orElse(null);

        if (game == null) {
            return null;
        }
        return gameMapper.toGameWithRatingDto(game);
    }

    // ========================================================================
    // 3. CÁC PHƯƠNG THỨC QUẢN TRỊ & PUBLISHER (FROM NHÁNH VANHAU)
    // ========================================================================


    @Override
    @Transactional
    public GameDto updateStatus(Long id, String status) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game không tồn tại"));
        try {
            SubmissionStatus s = SubmissionStatus.valueOf(status.trim().toUpperCase());
            game.setStatus(s);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ (PENDING|APPROVED|REJECTED)");
        }
        game = gameRepository.save(game);
        return gameMapper.toDTO(game);
    }


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
    public void rejectGame(Long submissionId, String reason) {
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
                .map(gameMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats() {
        // Các hàm này phải khớp với tên trong GameRepository
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
                    dto.setSubmittedDate("N/A");
                }
            }
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

    // ========================================================================
    // 4. CÁC PHƯƠNG THỨC CHO NGƯỜI DÙNG/CUSTOMER (FROM NHÁNH MAIN)
    // ========================================================================

    @Override
    @Transactional(readOnly = true)
    public boolean checkOwnership(String username, Long gameId) {
        // TODO: thay bằng logic của bạn nếu khác
        return customerRepository
                .existsByAccount_UsernameAndOwnedGames_Id(username, gameId);
    }

    @Override
    @Transactional
    public GameDto createPending(GameCreateRequest req, String publisherUsername) {

        // Tìm Publisher theo email
        Publisher publisher = publisherRepository.findByAccount_Username(publisherUsername)
                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));

        // 1️⃣ Tạo GameBasicInfo
        GameBasicInfo info = new GameBasicInfo();
        info.setName(req.getTitle());
        info.setShortDescription(req.getSummary());
        info.setDescription(req.getDescription());
        info.setThumbnail(req.getCoverUrl());
        info.setTrailerUrl(req.getTrailerUrl());
        info.setPrice(BigDecimal.valueOf(req.isFree() ? 0.0 : req.getPrice()));
        info.setIsSupportController(req.isSupportController());
        info.setRequiredAge(req.isAge18() ? 18 : 0);
        info.setPublisher(publisher);
        info.setFilePath(req.getFilePath());
        System.out.println("📁 File path nhận được từ frontend: " + req.getFilePath());


        // Category
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
            info.setCategory(category);
        }

        // Lưu trước info (để có id)
        info = gameBasicInfoRepository.save(info);

        // 2️⃣ Lưu platforms
        var platformEntities = new java.util.ArrayList<Platform>();
        for (String name : req.getPlatforms()) {
            platformEntities.add(
                    platformRepository.findByName(name.toUpperCase())
                            .orElseThrow(() -> new RuntimeException("Platform không hợp lệ: " + name))
            );
        }
        info.setPlatforms(platformEntities);

        // 3️⃣ Tạo Game (bản chính)
        Game game = new Game();
        game.setGameBasicInfos(info);
        game.setReleaseDate(req.getReleaseDate());
        game.setStatus(SubmissionStatus.PENDING);

        // Lưu vào DB
        game = gameRepository.save(game);

        return gameMapper.toDTO(game);
        // Sử dụng phương thức mới trong CustomerRepository để kiểm tra sự tồn tại
        return customerRepository.existsByAccount_UsernameAndOwnedGames_Id(username, gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findByStatus(String status) {
        List<Game> games = gameRepository.findByStatus(status); // ✅ cần repo
        return games.stream().map(gameMapper::toDTO).toList();
    }


    @Override
    public List<GameDto> getAllByGameToday() {
        List<Game> items = gameRepository.findAllByGameToday();
        return gameMapper.toGameDto(items);
    }
}