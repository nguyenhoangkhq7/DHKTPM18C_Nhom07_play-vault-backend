package fit.iuh.services;

import fit.iuh.dtos.GameCreateRequest;
import fit.iuh.dtos.GameDto;
import fit.iuh.dtos.GameSearchResponseDto;
import fit.iuh.dtos.*;
import fit.iuh.models.Game;
import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface GameService {

    List<GameDto> findAll();

    GameDto findById(Long id);

    List<GameDto> findGamesByCategoryName(String categoryName);

    List<GameDto> findTopRatedGames(int topN);

    Game findGameEntityById(Long id);

    List<GameWithRatingDto> getTopGamesWithRating(int topN);

    // Thêm method tìm kiếm nâng cao
    Page<GameSearchResponseDto> searchAndFilterGames(
            String keyword,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            SubmissionStatus status,
            Pageable pageable);

    GameWithRatingDto getGameWithRatingById(Long id);

    Page<GameSearchResponseDto> searchAndFilterPendingGames(Pageable pageable, String searchQuery);

    Page<GameSearchResponseDto> searchAndFilterApprovedGames(Pageable pageable, String searchQuery, String categoryFilter, String sortBy);

    GameDetailDto getGameForAdmin(Long gameId);

    GameDetailDto approveGame(Long gameId);

    void rejectGame(Long gameId, String reason);

    GameDetailDto updateApprovedGameStatus(Long gameId, String newStatus);

    List<GameDto> getGamesByPublisherId(Long publisherId);

    DashboardStatsResponse getDashboardStats();

    Page<GameSearchResponseDto> searchGameSubmissions(Pageable pageable, String searchQuery, String statusFilter);

    boolean checkOwnership(String username, Long gameId);

    // ========================================================================
    // 3. THÊM: LOGIC KIỂM TRA QUYỀN SỞ HỮU (Buy & Download)
    // ========================================================================
    @Transactional
    // ✅ Bản cũ: chỉ nhận JSON
    GameDto createPending(GameCreateRequest req, String username);

    // ✅ Bản có file: dùng khi controller multipart
    GameDto createPendingWithFile(GameCreateRequest req,
                                  MultipartFile thumbnailFile,
                                  String username) throws Exception;

    GameDto updateStatus(Long id, String status);
    // đã gợi ý từ trước
    List<GameDto> findByStatus(String status);        // ✅ thêm mới

    // GameService
    Optional<GameSubmission> getLatestSubmissionByGameId(Long gameId);

    List<GameDto> getAllByGameToday();

    // 👇 THÊM DÒNG NÀY: Để lấy tên file game (ví dụ: game_setup.rar) từ DB
    String getGameFileName(Long gameId);
//    List<Game> searchByName(String keyword);

//    List<Game> findBySystem(String os, String cpu, String gpu, Integer ram, Integer storage);

//    List<Game> searchAdvanced(
//            String os,
//            String cpu,
//            String gpu,
//            Integer ram,
//            Integer storage,
//            String keyword,
//            Long categoryId,
//            Double minRating,
//            Double maxPrice
//    );

    Page<GameSearchResponseDto> searchGamesSimple(String keyword, Pageable pageable);
}
