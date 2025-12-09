package fit.iuh.controllers;

import fit.iuh.services.R2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;
import fit.iuh.services.GameService; // <-- QUAN TRỌNG
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/r2")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class R2UploadController {

        private final GameService gameService;
        private final R2Service r2Service;

        /**
         * 🎯 API CHÍNH: Upload file trực tiếp qua backend
         * Test bằng Postman: POST http://localhost:8080/api/r2/upload
         * Body: form-data, key="file", chọn file
         */
        @PostMapping("/upload")
        public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
                try {
                        if (file.isEmpty()) {
                                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
                        }

                        // Tạo tên file unique
                        String originalFilename = file.getOriginalFilename();
                        String extension = originalFilename != null && originalFilename.contains(".")
                                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                                        : "";
                        String fileName = UUID.randomUUID().toString() + extension;

                        // Upload lên R2
                        String fileUrl = r2Service.uploadFile(file, fileName);

                        System.out.println("✅ File uploaded successfully: " + fileName);

                        return ResponseEntity.ok(Map.of(
                                        "success", true,
                                        "fileName", fileName,
                                        "fileUrl", fileUrl,
                                        "message", "File uploaded to Cloudflare R2"));

                } catch (Exception e) {
                        System.err.println("❌ Upload failed: " + e.getMessage());
                        e.printStackTrace();
                        return ResponseEntity.badRequest().body(Map.of(
                                        "success", false,
                                        "error", e.getMessage()));
                }
        }

        /**
         * 📤 API: Tạo presigned upload URL (cho frontend upload trực tiếp)
         */
        /**
         * 📤 API: Tạo link để Publisher upload file game (PUT)
         * URL: POST /api/r2/presigned-upload-url
         * Input: extension (đuôi file, ví dụ: .rar, .zip, .exe)
         */
        @PostMapping("/presigned-upload-url")
        public ResponseEntity<?> getPresignedUploadUrl(@RequestParam String extension) {
                try {
                        // 1. Tạo tên file ngẫu nhiên để không bị trùng
                        String uniqueFileName = "games/" + UUID.randomUUID().toString() + "."
                                        + extension.replace(".", "");

                        // 2. Tạo link upload (Link này cho phép PUT file lên trong vòng 15 phút)
                        String uploadUrl = r2Service.generateUploadUrl(uniqueFileName);

                        return ResponseEntity.ok(Map.of(
                                        "uploadUrl", uploadUrl, // Link để Frontend PUT file vào
                                        "filePath", uniqueFileName, // QUAN TRỌNG: Tên file này sẽ gửi kèm form tạo game
                                        "method", "PUT",
                                        "message", "Dùng method PUT để upload file vào link này."));
                } catch (Exception e) {
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        /**
         * 📥 API: Tạo presigned download URL
         */
        @GetMapping("/presigned-download-url")
        public ResponseEntity<?> getPresignedDownloadUrl(@RequestParam String fileName) {
                try {
                        String downloadUrl = r2Service.generateDownloadUrl(fileName);

                        return ResponseEntity.ok(Map.of(
                                        "downloadUrl", downloadUrl,
                                        "fileName", fileName));
                } catch (Exception e) {
                        System.err.println("❌ Error generating download URL: " + e.getMessage());
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        /**
         * 📋 API: List tất cả files trong bucket
         */
        @GetMapping("/files")
        public ResponseEntity<?> listFiles() {
                try {
                        var files = r2Service.listFiles();

                        return ResponseEntity.ok(Map.of(
                                        "files", files,
                                        "count", files.size()));
                } catch (Exception e) {
                        System.err.println("❌ List files error: " + e.getMessage());
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        /**
         * 🗑️ API: Xóa file
         */
        @DeleteMapping("/files/{fileName}")
        public ResponseEntity<?> deleteFile(@PathVariable String fileName) {
                try {
                        r2Service.deleteFile(fileName);

                        return ResponseEntity.ok(Map.of(
                                        "success", true,
                                        "message", "File deleted: " + fileName));
                } catch (Exception e) {
                        System.err.println("❌ Delete error: " + e.getMessage());
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }

        @GetMapping("/download-game/{gameId}")
        public ResponseEntity<?> secureDownloadGame(
                        @PathVariable Long gameId,
                        Authentication authentication // Lấy user từ Token JWT
        ) {
                // A. Kiểm tra đăng nhập
                if (authentication == null || !authentication.isAuthenticated()) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(Map.of("error", "Vui lòng đăng nhập để tải game."));
                }

                String username = authentication.getName(); // Lấy username người đang gọi API

                // B. Kiểm tra quyền sở hữu (Đã mua chưa?)
                boolean isOwned = gameService.checkOwnership(username, gameId);

                if (!isOwned) {
                        // ❌ NẾU CHƯA MUA -> CHẶN NGAY
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                                        "error", "Truy cập bị từ chối",
                                        "message", "Bạn chưa mua game này nên không thể tải."));
                }

                try {
                        // C. Lấy tên file thật từ DB
                        String r2FileName = gameService.getGameFileName(gameId);

                        // D. Tạo Link R2 (Link này sẽ tự hết hạn sau 5-15 phút do cấu hình bên
                        // R2Service)
                        String downloadUrl = r2Service.generateDownloadUrl(r2FileName);

                        return ResponseEntity.ok(Map.of(
                                        "success", true,
                                        "gameId", gameId,
                                        "downloadUrl", downloadUrl,
                                        "message", "Link tải hợp lệ trong thời gian ngắn."));
                } catch (Exception e) {
                        System.err.println("❌ Download Error: " + e.getMessage());
                        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
                }
        }
}