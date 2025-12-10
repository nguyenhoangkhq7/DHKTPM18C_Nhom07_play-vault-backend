package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.mappers.GameMapper;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.enums.SubmissionStatus;
import fit.iuh.repositories.GameRepository;
import fit.iuh.services.GameBasicInfoService;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.dtos.ReviewDto;
import fit.iuh.services.GameService;
import fit.iuh.services.GameVectorService;
import fit.iuh.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication; // Thêm import Authentication
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final ReviewService reviewService;
    private final GameVectorService gameVectorService; // Inject service mới
    private final GameRepository gameRepository;
    private final GameMapper gameMapper;

    @GetMapping
    public ResponseEntity<List<GameDto>> getGames(
            @RequestParam(required = false) String categoryName) {
        List<GameDto> games = gameService.findGamesByCategoryName(categoryName);
        return ResponseEntity.ok(games);
    }

    @GetMapping("/top")
    public ResponseEntity<List<GameWithRatingDto>> getTopRatedGames(
            @RequestParam(defaultValue = "0") int limit) {
        List<GameWithRatingDto> gameWithRatingDtos= gameService.getTopGamesWithRating(limit);
        return ResponseEntity.ok(gameWithRatingDtos);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<GameSearchResponseDto>> searchGames(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) SubmissionStatus status,
            @PageableDefault(size = 12) Pageable pageable // Mặc định 12 game/trang
    ) {
        if (status == null) {
            status = SubmissionStatus.APPROVED;
        }

        Page<GameSearchResponseDto> games = gameService.searchAndFilterGames(
                keyword, categoryId, minPrice, maxPrice, status, pageable);

        return ResponseEntity.ok(games);
    }

    @GetMapping("/search-for")
    public ResponseEntity<Page<GameSearchResponseDto>> searchGames(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 12) Pageable pageable
    ) {
        // Gọi Service xử lý logic tìm kiếm chuẩn
        Page<GameSearchResponseDto> result = gameService.searchGamesSimple(keyword, pageable);

        return ResponseEntity.ok(result);
    }

    // ========================================================================
    // SỬA: Lấy chi tiết Game, kiểm tra quyền sở hữu (Buy & Download Logic)
    // ========================================================================
//    @GetMapping("/{id}")
//    public ResponseEntity<fit.iuh.dtos.GameDetailDto> getGameDetail(
//            @PathVariable Long id,
//            Authentication authentication // Inject thông tin user đang đăng nhập
//    ) {
//        fit.iuh.models.Game game = gameService.findGameEntityById(id);
//
//        if (game == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        boolean isOwned = false;
//
//        // 1. Nếu người dùng đã đăng nhập và là Customer, kiểm tra quyền sở hữu
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName();
//            // TODO: Bổ sung logic kiểm tra xem user này có phải là Customer không (nếu cần)
//            isOwned = gameService.checkOwnership(username, id);
//        }
//
//        // 2. Trả về DTO, truyền cờ sở hữu vào phương thức fromEntity
//        return ResponseEntity.ok(fit.iuh.dtos.GameDetailDto.fromEntity(game, isOwned));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailDto> getGameDetail(
            @PathVariable Long id,
            Authentication authentication) {

        var gameEntity = gameService.findGameEntityById(id);
        if (gameEntity == null) return ResponseEntity.notFound().build();

        boolean isOwned = false;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            isOwned = gameService.checkOwnership(username, id); // check ownership
        }

        GameDetailDto dto = GameDetailDto.fromEntityIsOwned(gameEntity, isOwned);
        // ➜ Bổ sung status + submittedDate từ GameSubmission
        gameService.getLatestSubmissionByGameId(id).ifPresent(sub -> {
            dto.setStatus(sub.getStatus() != null ? sub.getStatus().name() : null);
            dto.setSubmittedDate(sub.getSubmittedAt() != null ? sub.getSubmittedAt().toString() : null);
        });
        return ResponseEntity.ok(dto);
    }


    // ========================================================================
    // Giữ nguyên các phương thức còn lại
    // ========================================================================
    @GetMapping("card/{id}")
    public ResponseEntity<GameWithRatingDto> getGameById(@PathVariable Long id) {
        GameWithRatingDto gameDto = gameService.getGameWithRatingById(id);

        if (gameDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(gameDto);
    }

    /**
     * API 1: Chạy 1 lần để đồng bộ toàn bộ dữ liệu vào Vector Store
     * URL: POST http://localhost:8080/api/games/sync-vector
     */
    @PostMapping("/sync-vector")
    public ResponseEntity<String> syncVector() {
        List<fit.iuh.models.Game> allGames = gameRepository.findAllExcludingPendingSubmissions();

        if (allGames.isEmpty()) {
            return ResponseEntity.ok("Không có game nào để đồng bộ.");
        }

        gameVectorService.addGames(allGames);
        return ResponseEntity.ok("Đã đồng bộ thành công " + allGames.size() + " game vào AI Vector Store!");
    }

    /**
     * API 2: Tìm kiếm thông minh bằng AI
     * URL: GET http://localhost:8080/api/games/search-ai?query=game bắn súng hay
     */
    @GetMapping("/search-ai")
    public ResponseEntity<List<GameSearchResponseDto>> searchSemantic(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0.5") double threshold) { // Cho phép chỉnh ngưỡng từ API

        // 1. Lấy danh sách ID đã được AI sắp xếp theo độ giống
        List<Long> aiSortedIds = gameVectorService.searchGameIds(keyword, limit, threshold);

        if (aiSortedIds.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // 2. Lấy dữ liệu từ DB (kết quả trả về của MySQL thường không theo thứ tự ID mình đưa vào)
        List<Game> gamesFromDb = gameRepository.findAllById(aiSortedIds);

        // 3. Tối ưu: Map ID sang Game Object để truy xuất nhanh (O(1)) thay vì Loop lồng nhau (O(n^2))
        Map<Long, Game> gameMap = gamesFromDb.stream()
                .collect(Collectors.toMap(Game::getId, Function.identity()));

        // 4. Sắp xếp lại danh sách kết quả theo đúng thứ tự của AI trả về
        List<GameSearchResponseDto> result = aiSortedIds.stream()
                .filter(gameMap::containsKey) // Đảm bảo ID có trong DB
                .map(gameMap::get)            // Lấy Game từ Map
                .map(gameMapper::toSearchResponseDto) // Convert sang DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<GameDto> createGame(@RequestBody GameCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 👈 lấy username từ token đang đăng nhập

        GameDto saved = gameService.createPending(request, username);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status) {
        GameDto updated = gameService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingGames() {
        List<GameDto> list = gameService.findByStatus("PENDING");
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search-combined")
    public ResponseEntity<Page<GameSearchResponseDto>> searchCombined(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0.5") double threshold,
            @PageableDefault(size = 12) Pageable pageable
    ) {

        boolean emptyKeyword = (keyword == null || keyword.trim().isEmpty());

        // ============================================================
        // 1. Nếu keyword rỗng => chỉ chạy search DB + phân trang bình thường
        // ============================================================
        if (emptyKeyword) {
            Page<GameSearchResponseDto> page = gameService.searchGamesSimple(null, pageable);
            return ResponseEntity.ok(page);
        }

        // ============================================================
        // 2. Nếu có keyword => chạy combined search (DB + AI)
        // ============================================================
        // 2.1 Lấy ID từ DB
        Page<GameSearchResponseDto> dbResult =
                gameService.searchGamesSimple(keyword, Pageable.unpaged());

        List<Long> dbIds = dbResult.getContent()
                .stream()
                .map(GameSearchResponseDto::getId)
                .toList();

        // 2.2 Lấy ID từ AI
        int aiLimit = 60;
        List<Long> aiIds = gameVectorService.searchGameIds(
                keyword,
                aiLimit,
                threshold
        );

        // 2.3 Gộp kết quả, ưu tiên DB trước
        LinkedHashSet<Long> mergedIds = new LinkedHashSet<>();
        mergedIds.addAll(dbIds);
        mergedIds.addAll(aiIds);

        List<Long> sortedIds = new ArrayList<>(mergedIds);

        // ============================================================
        // 3. Phân trang thủ công
        // ============================================================
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedIds.size());

        if (start >= sortedIds.size()) {
            return ResponseEntity.ok(new PageImpl<>(List.of(), pageable, sortedIds.size()));
        }

        List<Long> pageIds = sortedIds.subList(start, end);

        // ============================================================
        // 4. Lấy dữ liệu chi tiết
        // ============================================================
        List<Game> games = gameRepository.findAllById(pageIds);

        Map<Long, Game> map = games.stream()
                .collect(Collectors.toMap(Game::getId, Function.identity()));

        List<GameSearchResponseDto> result = pageIds.stream()
                .filter(map::containsKey)
                .map(map::get)
                .map(gameMapper::toSearchResponseDto)
                .toList();

        return ResponseEntity.ok(new PageImpl<>(result, pageable, sortedIds.size()));
    }



}