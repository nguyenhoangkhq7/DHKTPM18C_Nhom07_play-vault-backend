package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.services.GameService;
import fit.iuh.services.PublisherService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;
    private final GameService gameService;
    // API mới: POST http://localhost:8080/api/publishers/register
    @PostMapping("/register")
    public ResponseEntity<String> registerPublisher(@Valid @RequestBody PublisherRegisterRequest request) {
        try {
            // Gọi Service để xử lý Transaction (Lưu Account -> Payment -> Publisher -> Request)
            publisherService.registerPublisher(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đăng ký thành công! Vui lòng chờ Admin duyệt.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 1. API lấy số liệu tổng quan (Dashboard Top & Right Cards)
    @GetMapping("/{publisherId}/stats")
    public ResponseEntity<PublisherDashboardDto> getStats(@PathVariable Long publisherId) {
        return ResponseEntity.ok(publisherService.getPublisherDashboardStats(publisherId));
    }

    // 2. API lấy dữ liệu biểu đồ (Chart)
    @GetMapping("/{publisherId}/revenue-chart")
    public ResponseEntity<List<RevenueChartDto>> getRevenueChart(
            @PathVariable Long publisherId,
            @RequestParam(defaultValue = "2025") Integer year) {
        return ResponseEntity.ok(publisherService.getRevenueChart(publisherId, year));
    }

    // 3. API lấy danh sách game của Publisher (Hiển thị list game để chọn Edit)
    @GetMapping("/{publisherId}/games")
    public ResponseEntity<List<GameDto>> getMyGames(@PathVariable Long publisherId) {
        // Gọi service lấy list game theo publisherId
        return ResponseEntity.ok(gameService.getGamesByPublisherId(publisherId));
    }
    @PutMapping("/{publisherId}/games/{gameId}") // Phần đuôi URL
    public ResponseEntity<GameDto> updateGame(
            @PathVariable Long publisherId, // Hứng số 1 đầu tiên
            @PathVariable Long gameId,      // Hứng số 1 thứ hai
            @RequestBody GameUpdateDto request) {

        return ResponseEntity.ok(publisherService.updateGameByPublisher(publisherId, gameId, request));
    }
    @GetMapping
    public ResponseEntity<List<PublisherDto>> getPublisher(){

        return ResponseEntity.ok(publisherService.findAll());
    }

    @GetMapping("/by-username/{username}/profile")
    public ResponseEntity<PublisherDto> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(publisherService.getProfileByUsername(username));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<PublisherDto> update(@PathVariable Long id,
                                               @RequestBody PublisherDto dto) {
        return ResponseEntity.ok(publisherService.updateProfile(id, dto));
    }

    @PostMapping("/games")
    public ResponseEntity<?> create(@RequestBody @Valid GameCreateRequest req,
                                    Authentication auth) {
        var dto = gameService.createPending(req, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PostMapping(
            value = "/api/publishers/games/pending",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<GameDto> createPendingMultipart(
            @ModelAttribute GameCreateRequest req,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "galleryFiles", required = false) List<MultipartFile> galleryFiles,
            Principal principal
    ) throws Exception {
        req.setGalleryFiles(galleryFiles);
        return ResponseEntity.ok(
                gameService.createPendingWithFile(req, thumbnailFile, principal.getName())
        );
    }

    @PostMapping(
            value = "/games/pending",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GameDto> createPendingJson(
            @RequestBody GameCreateRequest req,
            Principal principal
    ) throws Exception {
        // Không có file => truyền null cho thumbnailFile
        GameDto dto = gameService.createPendingWithFile(req, null, principal.getName());
        return ResponseEntity.ok(dto);
    }



}
