package fit.iuh.services.impl;

import fit.iuh.dtos.GameCreateRequest;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.*;
import fit.iuh.models.enums.Os;
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
import fit.iuh.services.DriveService;
import fit.iuh.services.GameService;
import fit.iuh.services.GameVectorService;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// import
import org.springframework.web.multipart.MultipartFile;
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameMapper gameMapper; // MapStruct


    private final GameSubmissionRepository submissionRepository;

    // Dependency từ nhánh vanhau (Xử lý duyệt game)
    private final GameSubmissionRepository gameSubmissionRepository;

    // Dependency từ nhánh main (Xử lý thông tin khách hàng/sở hữu)
    private final CustomerRepository customerRepository;

    private final PublisherRepository publisherRepository;
    private final CategoryRepository categoryRepository;
    private final PlatformRepository platformRepository;

    private final SystemRequirementRepository systemRequirementRepository;
    // Nếu bạn lưu GameBasicInfo qua cascade từ Game thì KHÔNG cần repo này.
    // Nếu bạn lưu riêng, hãy khai báo:
    private final GameBasicInfoRepository gameBasicInfoRepository;
    private final DriveService driveUploader;
    private final PreviewImageRepository previewImageRepository;

    private final GameVectorService gameVectorService;
    private final OrderRepository orderRepository;

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
            SubmissionStatus status,
            Pageable pageable) {

        BigDecimal minPriceBd = minPrice != null ? BigDecimal.valueOf(minPrice) : null;
        BigDecimal maxPriceBd = maxPrice != null ? BigDecimal.valueOf(maxPrice) : null;

        Specification<Game> spec = GameSpecification.filterBy(keyword, categoryId, minPriceBd, maxPriceBd, status);

        // Lấy danh sách Game từ DB
        Page<Game> gamePage = gameRepository.findAll(spec, pageable);

        // Map sang DTO (Lúc này logic tính toán Rating trong DTO sẽ chạy)
        //return gamePage.map(GameSearchResponseDto::fromEntity);
        return gamePage.map(gameMapper::toSearchResponseDto);
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
        List<Game> allGames = gameRepository.findAllExcludingPendingSubmissions();
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
        final SubmissionStatus s;
        try { s = SubmissionStatus.valueOf(status.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { throw new RuntimeException("Trạng thái không hợp lệ (PENDING|APPROVED|REJECTED)"); }

        // id là gameId (game_basic_info_id) *hoặc* submissionId
        Optional<GameSubmission> subOpt = submissionRepository
                .findFirstByGameBasicInfos_IdOrderBySubmittedAtDesc(id);
        if (subOpt.isEmpty()) {
            // thử coi id là submissionId
            subOpt = submissionRepository.findById(id);
        }

        GameSubmission sub;
        Game game;

        if (subOpt.isPresent()) {
            sub = subOpt.get();
            Long gameId = sub.getGameBasicInfos().getId();
            game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game không tồn tại: " + gameId));
        } else {
            // ❗ KHÔNG TÌM THẤY submission ⇒ coi id là gameId và tạo mới
            game = gameRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Game không tồn tại: " + id));

            sub = new GameSubmission();
            sub.setGameBasicInfos(game.getGameBasicInfos());
            sub.setSubmittedAt(LocalDate.now()); // thời điểm tạo submission đầu tiên
        }

        // cập nhật trạng thái
        sub.setStatus(s);
        if (s == SubmissionStatus.APPROVED || s == SubmissionStatus.REJECTED) {
            sub.setReviewedAt(LocalDate.now());
            // sub.setReviewerUsername(currentAdmin);
        } else {
            sub.setReviewedAt(null);
            sub.setReviewerUsername(null);
            sub.setRejectReason(null);
        }
        submissionRepository.save(sub);

        GameDto dto = gameMapper.toDTO(game);
        dto.setStatus(s.name());
        return dto;
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
            return gameRepository.findGamesSortedByRevenue(search, category, SubmissionStatus.APPROVED, pageable);
        }
        if ("downloads_desc".equalsIgnoreCase(sortBy)) {
            return gameRepository.findGamesSortedByDownloads(search, category, SubmissionStatus.APPROVED, pageable);
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
        // 1. Tìm yêu cầu duyệt (Submission)
        GameSubmission submission = gameSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu duyệt"));

        // 2. Validate trạng thái
        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ có thể duyệt game đang ở trạng thái PENDING");
        }

        // 3. Cập nhật trạng thái Submission -> APPROVED
        Account admin = new Account();
        admin.setUsername("admin"); // TODO: Lấy admin thực tế từ SecurityContext nếu cần

        submission.setStatus(SubmissionStatus.APPROVED);
        submission.setReviewerUsername(admin);
        submission.setReviewedAt(LocalDate.now());

        // Lưu thay đổi vào bảng game_submissions
        gameSubmissionRepository.save(submission);

        // 4. Lấy thông tin Game đã tồn tại (KHÔNG TẠO MỚI)
        // Vì lúc createPending đã tạo Game rồi, giờ ta chỉ cần tìm lại nó.
        Long basicInfoId = submission.getGameBasicInfos().getId();

        // Tìm game dựa trên GameBasicInfo ID
        // Lưu ý: Bạn cần đảm bảo GameRepository có hàm findByGameBasicInfos_Id
        // Hoặc nếu bảng Games dùng chung ID với BasicInfo thì dùng findById(basicInfoId)
        Game existingGame = gameRepository.findByGameBasicInfos_Id(basicInfoId);

        if (existingGame == null) {
            // Fallback: Thử tìm bằng ID nếu cấu hình OneToOne @MapsId
            existingGame = gameRepository.findById(basicInfoId)
                    .orElseThrow(() -> new RuntimeException("Lỗi dữ liệu: Không tìm thấy Game gốc trong database"));
        }

        // (Tuỳ chọn) Cập nhật ngày phát hành chính thức là ngày duyệt
        existingGame.setReleaseDate(LocalDate.now());

        gameVectorService.addGames(List.of(existingGame));

        // Lưu cập nhật Game (nếu có thay đổi) và trả về DTO
        return GameDetailDto.fromEntity(gameRepository.save(existingGame));
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
        // 1. Tìm trong bảng Game trước
        Optional<Game> gameOpt = gameRepository.findById(id);

        if (gameOpt.isPresent()) {
            Game game = gameOpt.get();
            GameDetailDto dto = GameDetailDto.fromEntity(game);

            // --- QUAN TRỌNG: Lấy status thực sự từ bảng GameSubmission ---
            if (game.getGameBasicInfos() != null) {
                // Tìm submission mới nhất của game này
                Optional<GameSubmission> submissionOpt = gameSubmissionRepository
                        .findFirstByGameBasicInfos_IdOrderBySubmittedAtDesc(game.getGameBasicInfos().getId());

                if (submissionOpt.isPresent()) {
                    GameSubmission sub = submissionOpt.get();

                    // Đè status của Game bằng status của Submission (PENDING/REJECTED/APPROVED)
                    dto.setStatus(sub.getStatus().name());

                    // Map thêm thông tin khác
                    dto.setSubmittedDate(sub.getSubmittedAt() != null ? sub.getSubmittedAt().toString() : "N/A");
                    if (sub.getStatus() == SubmissionStatus.REJECTED) {
                        // Nếu DTO có trường rejectReason thì set vào, nếu không thì bỏ qua
                        // dto.setRejectReason(sub.getRejectReason());
                    }
                } else {
                    dto.setSubmittedDate("N/A");
                }
            }
            return dto;
        }

        // 2. Nếu không có trong bảng Game, tìm trực tiếp trong GameSubmission (trường hợp data cũ hoặc lỗi)
        Optional<GameSubmission> submissionOpt = gameSubmissionRepository.findById(id);
        if (submissionOpt.isPresent()) {
            return GameDetailDto.fromSubmissionEntity(submissionOpt.get());
        }

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

//    @Override
//    @Transactional
//    public GameDto createPending(GameCreateRequest req, String username) {
//        // 1️⃣ Lấy Category
//        Category category = categoryRepository.findById(req.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Category không tồn tại"));
//
//        // 2️⃣ Lấy Publisher theo account
//        Publisher publisher = publisherRepository.findByAccount_Username(username)
//                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại cho tài khoản: " + username));
//
//        // 3️⃣ Tạo SystemRequirement
//        var srDto = req.getSystemRequirement();  // DTO có getter Lombok
//        SystemRequirement sr = new SystemRequirement();
//        sr.setOs(srDto.getOs());          // dùng enum Os trực tiếp
//        sr.setCpu(srDto.getCpu());
//        sr.setGpu(srDto.getGpu());
//        sr.setStorage(srDto.getStorage());
//        sr.setRam(srDto.getRam());
//        sr = systemRequirementRepository.save(sr);
//
//        // 4️⃣ Tạo GameBasicInfo
//        GameBasicInfo gbi = new GameBasicInfo();
//        gbi.setName(req.getName());
//        gbi.setShortDescription(req.getShortDescription());
//        gbi.setDescription(req.getDescription()); // ghi chú phát hành
//        gbi.setPrice(req.getPrice());
//        gbi.setTrailerUrl(req.getTrailerUrl());
//        gbi.setRequiredAge(req.getRequiredAge());
//        gbi.setFilePath(req.getFilePath());
//        gbi.setIsSupportController(Boolean.TRUE.equals(req.getIsSupportController()));
//        gbi.setCategory(category);
//        gbi.setPublisher(publisher);
//        gbi.setSystemRequirement(sr);
//
//        // 5️⃣ Chuẩn hoá thumbnail (nếu có link Drive)
//        if (req.getThumbnail() != null && !req.getThumbnail().isBlank()) {
//            String thumbUrl = req.getThumbnail();
//            String id = extractGoogleDriveId(thumbUrl);
//            if (id != null) {
//                // ✅ Chuyển link drive sang link ảnh public
//                thumbUrl = "https://lh3.googleusercontent.com/d/" + id + "=w1200";
//            }
//            gbi.setThumbnail(thumbUrl);
//        }
//
//        // 6️⃣ Gán platforms
//        if (req.getPlatformIds() != null && !req.getPlatformIds().isEmpty()) {
//            var platforms = platformRepository.findAllById(req.getPlatformIds());
//            if (platforms.size() != req.getPlatformIds().size()) {
//                throw new RuntimeException("platformIds chứa id không tồn tại");
//            }
//            gbi.setPlatforms(platforms);
//        }
//
//        gbi = gameBasicInfoRepository.save(gbi);
//
//        // 7️⃣ Tạo Game
//        Game game = new Game();
//        game.setGameBasicInfos(gbi);
//        game.setReleaseDate(req.getReleaseDate());
//        game = gameRepository.save(game);
//
//        // 8️⃣ Tạo Submission (Pending)
//        GameSubmission submission = new GameSubmission();
//        submission.setGameBasicInfos(gbi);
//        submission.setStatus(SubmissionStatus.PENDING);
//        submission.setSubmittedAt(LocalDate.now());
//        gameSubmissionRepository.save(submission);
//
//        // 9️⃣ Trả DTO
//        GameDto dto = gameMapper.toDTO(game);
//        dto.setStatus(SubmissionStatus.PENDING.name());
//        return dto;
//    }
//@Override
//@Transactional
//public GameDto createPending(GameCreateRequest req, String publisherUsername) {
//    Publisher publisher = publisherRepository.findByAccount_Username(publisherUsername)
//            .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));
//
//
//    // Tạo & lưu GameBasicInfo
//    GameBasicInfo info = new GameBasicInfo();
//    info.setName(req.getTitle());
//    info.setShortDescription(req.getSummary());
//    info.setDescription(req.getDescription());
//    info.setThumbnail(req.getCoverUrl());
//    info.setTrailerUrl(req.getTrailerUrl());
//    info.setPrice(BigDecimal.valueOf(req.isFree() ? 0.0 : req.getPrice()));
//    info.setIsSupportController(req.isSupportController());
//    info.setRequiredAge(req.isAge18() ? 18 : 0);
//    info.setPublisher(publisher);
//    info.setFilePath(req.getFilePath());
//
//    if (req.getCategoryId() != null) {
//        Category category = categoryRepository.findById(req.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
//        info.setCategory(category);
//    }
//    info = gameBasicInfoRepository.save(info);
//
//    // Platforms
//    var platforms = new java.util.ArrayList<Platform>();
//    for (String name : req.getPlatforms()) {
//        platforms.add(platformRepository.findByName(name.toUpperCase())
//                .orElseThrow(() -> new RuntimeException("Platform không hợp lệ: " + name)));
//    }
//    info.setPlatforms(platforms);
//
//    // Tạo & lưu Game (id = game_basic_info_id)
//    Game game = new Game();
//    game.setGameBasicInfos(info);
//    game.setReleaseDate(req.getReleaseDate());
//    game = gameRepository.save(game);
//
//    // Tạo submission PENDING
//    GameSubmission sub = new GameSubmission();
//    sub.setGameBasicInfos(info);            // đúng field theo entity bạn gửi
//    sub.setStatus(SubmissionStatus.PENDING);
//    sub.setSubmittedAt(LocalDate.now());
//    submissionRepository.save(sub);
//
//    // Trả DTO (đơn giản): map rồi set status thủ công
//    GameDto dto = gameMapper.toDTO(game);
//    dto.setStatus(SubmissionStatus.PENDING.name());
//    return dto;
//}
@Transactional
public GameDto createPending(GameCreateRequest req, String publisherUsername) {
    Publisher publisher = publisherRepository.findByAccount_Username(publisherUsername)
            .orElseThrow(() -> new RuntimeException("Publisher không tồn tại"));

    // ✅ 1. TẠO SYSTEM REQUIREMENT (nếu có dữ liệu)
    SystemRequirement sr = null;
    if (req.getSystemRequirement() != null) {
        var srDto = req.getSystemRequirement();
        sr = new SystemRequirement();
        // Bạn có thể mở rộng để hỗ trợ nhiều OS sau, hiện tại mặc định Windows hoặc lấy từ DTO nếu thêm field
        sr.setOs(Os.WINDOWS); // hoặc srDto.getOs() nếu bạn thêm field os vào DTO
        sr.setCpu(srDto.getCpu());
        sr.setGpu(srDto.getGpu());
        sr.setRam(srDto.getRam());
        sr.setStorage(srDto.getStorage());
        sr = systemRequirementRepository.save(sr); // lưu để có ID
    }

    // ✅ 2. Tạo & lưu GameBasicInfo
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

    // Gán category
    if (req.getCategoryId() != null) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category"));
        info.setCategory(category);
    }

    // ✅ QUAN TRỌNG: GÁN SYSTEM REQUIREMENT
    info.setSystemRequirement(sr);

    info = gameBasicInfoRepository.save(info);

    // Platforms
    var platforms = new ArrayList<Platform>();
    for (String name : req.getPlatforms()) {
        platforms.add(platformRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Platform không hợp lệ: " + name)));
    }
    info.setPlatforms(platforms);
    info = gameBasicInfoRepository.save(info); // lưu lại để cập nhật platforms

    // Tạo Game
    Game game = new Game();
    game.setGameBasicInfos(info);
    game.setReleaseDate(req.getReleaseDate());
    game = gameRepository.save(game);

    // Tạo submission PENDING
    GameSubmission sub = new GameSubmission();
    sub.setGameBasicInfos(info);
    sub.setStatus(SubmissionStatus.PENDING);
    sub.setSubmittedAt(LocalDate.now());
    submissionRepository.save(sub);

    GameDto dto = gameMapper.toDTO(game);
    dto.setStatus(SubmissionStatus.PENDING.name());
    return dto;
}


    @Override
    public GameDto createPendingWithFile(GameCreateRequest req, MultipartFile thumbnailFile, String username) throws Exception {
        return null;
    }


    private String extractGoogleDriveId(String url) {
        if (url == null || url.isBlank()) return null;

        // /file/d/<ID>/view?...
        var m1 = java.util.regex.Pattern.compile("/file/d/([A-Za-z0-9_-]{20,})").matcher(url);
        if (m1.find()) return m1.group(1);

        // ?id=<ID>
        var m2 = java.util.regex.Pattern.compile("[?&]id=([A-Za-z0-9_-]{20,})").matcher(url);
        if (m2.find()) return m2.group(1);

        // không khớp
        return null;
    }

    /** Bản đầy đủ: hỗ trợ upload thumbnail file (multipart) */
//    @Transactional
//    public GameDto createPendingWithFile(GameCreateRequest req, MultipartFile thumbnailFile,String username) throws Exception {
//        // 1) Category
//        Category category = categoryRepository.findById(req.getCategoryId())
//                .orElseThrow(() -> new RuntimeException("Category không tồn tại"));
//
//        // 2) Publisher
//        Publisher publisher = publisherRepository.findByAccount_Username(username)
//                .orElseThrow(() -> new RuntimeException("Publisher không tồn tại cho tài khoản: " + username));
//
//        // 3) SystemRequirement
//        var srDto = req.getSystemRequirement();
//        SystemRequirement sr = new SystemRequirement();
//        sr.setOs(srDto.getOs());
//        sr.setCpu(srDto.getCpu());
//        sr.setGpu(srDto.getGpu());
//        sr.setStorage(srDto.getStorage());
//        sr.setRam(srDto.getRam());
//        sr = systemRequirementRepository.save(sr);
//
//        // 4) GameBasicInfo
//        GameBasicInfo gbi = new GameBasicInfo();
//        gbi.setName(req.getName());
//        gbi.setShortDescription(req.getShortDescription());
//        gbi.setDescription(req.getDescription());      // ghi chú phát hành
//        gbi.setPrice(req.getPrice());
//        gbi.setTrailerUrl(req.getTrailerUrl());
//        gbi.setRequiredAge(req.getRequiredAge());
//        gbi.setFilePath(req.getFilePath());            // nếu bạn có lưu đường dẫn build
//        gbi.setIsSupportController(Boolean.TRUE.equals(req.getIsSupportController()));
//        gbi.setCategory(category);
//        gbi.setPublisher(publisher);
//        gbi.setSystemRequirement(sr);
//
//        // 4.1) Thumbnail:
//        // - Nếu có file upload → upload lên Drive và set URL lh3
//        // - Nếu không có file mà req.getThumbnail() có sẵn link → chuẩn hoá sang lh3 (nếu là link Drive)
//        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
//            String embeddableUrl = driveUploader.uploadImageAndGetEmbeddableUrl(thumbnailFile);
//            gbi.setThumbnail(embeddableUrl); // https://lh3.googleusercontent.com/d/<ID>=w1200
//        } else if (req.getThumbnail() != null && !req.getThumbnail().isBlank()) {
//            gbi.setThumbnail(DriveLinkUtil.toEmbeddableIfDriveUrl(req.getThumbnail()));
//        }
//
//        // 5) Platforms
//        if (req.getPlatformIds() != null && !req.getPlatformIds().isEmpty()) {
//            var platforms = platformRepository.findAllById(req.getPlatformIds());
//            if (platforms.size() != req.getPlatformIds().size())
//                throw new RuntimeException("platformIds chứa id không tồn tại");
//            gbi.setPlatforms(platforms);  // đúng với entity của bạn
//        }
//
//        gbi = gameBasicInfoRepository.save(gbi);
//
//
//        // 5.1) Preview images (gallery)
//        List<PreviewImage> images = new ArrayList<>();
//
//        // a) từ file upload
//        if (req.getGalleryFiles() != null) {
//            for (MultipartFile f : req.getGalleryFiles()) {
//                if (f != null && !f.isEmpty()) {
//                    String url = driveUploader.uploadImageAndGetEmbeddableUrl(f); // ra lh3
//                    PreviewImage pi = new PreviewImage();
//                    pi.setGameBasicInfo(gbi);
//                    pi.setUrl(url);
//                    gbi.getPreviewImages().add(pi);
//                }
//            }
//        }
//
//        // b) từ link có sẵn trong request
//        if (req.getGallery() != null) {
//            for (String raw : req.getGallery()) {
//                if (raw != null && !raw.isBlank()) {
//                    String url = DriveLinkUtil.toEmbeddableIfDriveUrl(raw);
//                    PreviewImage pi = new PreviewImage();
//                    pi.setGameBasicInfo(gbi);
//                    pi.setUrl(url);
//                    gbi.getPreviewImages().add(pi);
//                }
//            }
//        }
//
//        // 6) Game
//        Game game = new Game();
//        game.setGameBasicInfos(gbi);
//        game.setReleaseDate(req.getReleaseDate());
//        game = gameRepository.save(game);
//
//        // 7) Submission
//        GameSubmission submission = new GameSubmission();
//        submission.setGameBasicInfos(gbi);
//        submission.setStatus(SubmissionStatus.PENDING);
//        submission.setSubmittedAt(java.time.LocalDate.now());
//        gameSubmissionRepository.save(submission);
//
//        // 8) DTO
//        GameDto dto = gameMapper.toDTO(game);
//        dto.setStatus(SubmissionStatus.PENDING.name());
//        return dto;
//    }






    @Override
    @Transactional(readOnly = true)
    public List<GameDto> findByStatus(String status) {
        SubmissionStatus s;
        try {
            s = SubmissionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Trạng thái không hợp lệ (PENDING|APPROVED|REJECTED)");
        }

        // lấy các submission theo status, suy ra id game (= game_basic_info_id)
        var subs = submissionRepository.findByStatus(s);
        var ids  = subs.stream()
                .map(gs -> gs.getGameBasicInfos().getId())
                .toList();

        if (ids.isEmpty()) return java.util.Collections.emptyList();

        var games = gameRepository.findAllById(ids);
        var dtos  = games.stream().map(gameMapper::toDTO).toList();
        // set status thủ công (vì mapper không map status)
        dtos.forEach(d -> d.setStatus(s.name()));
        return dtos;
    }



    @Override
    public List<GameDto> getAllByGameToday() {
        List<Game> items = gameRepository.findAllByGameToday();
        return gameMapper.toGameDto(items);
    }

//    @Override
//    public List<Game> searchByName(String keyword) {
//        return gameRepository.searchByName(keyword);
//    }

//    @Override
//    public List<Game> findBySystem(String os, String cpu, String gpu, Integer ram, Integer storage) {
//        return gameRepository.findBySystem(os, cpu, gpu, ram, storage);
//    }

//    @Override
//    public List<Game> searchAdvanced(String os, String cpu, String gpu, Integer ram, Integer storage, String keyword, Long categoryId, Double minRating, Double maxPrice) {
//        return gameRepository.searchAdvanced(os, cpu, gpu, ram, storage, keyword, categoryId, minRating, maxPrice);
//    }

    // GameServiceImpl
    @Override
    @Transactional(readOnly = true)
    public Optional<GameSubmission> getLatestSubmissionByGameId(Long gameId) {
        return submissionRepository.findFirstByGameBasicInfos_IdOrderBySubmittedAtDesc(gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getGameFileName(Long gameId) {
        // 1. Tìm Game theo ID
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game không tồn tại với ID: " + gameId));

        // 2. Lấy thông tin cơ bản
        if (game.getGameBasicInfos() == null) {
            throw new RuntimeException("Dữ liệu game bị lỗi (thiếu Basic Info)");
        }

        // 3. Lấy đường dẫn file (FilePath) đã lưu khi Upload
        String filePath = game.getGameBasicInfos().getFilePath();

        if (filePath == null || filePath.isEmpty()) {
            throw new RuntimeException("Game này chưa được upload file cài đặt.");
        }

        return filePath;
    }

    @Override
    public Page<GameSearchResponseDto> searchGamesSimple(String keyword, Pageable pageable) {
        // Gọi repository
        Page<Game> gamePage = gameRepository.searchByKeyword(keyword, pageable);

        // Convert Entity sang DTO
        return gamePage.map(gameMapper::toSearchResponseDto);
    }

}