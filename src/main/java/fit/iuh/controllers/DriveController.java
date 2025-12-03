package fit.iuh.controllers;

import com.google.api.services.drive.model.File;
import fit.iuh.services.DriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/drive")
@RequiredArgsConstructor
public class DriveController {

    private final DriveService driveService;

    /**
     * Upload file lên Google Drive (Gmail cá nhân, dùng OAuth 2.0)
     * @param file file multipart
     * @param publicRead nếu = true, file sẽ được chia sẻ công khai (anyone-reader)
     */
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(name = "public", defaultValue = "false") boolean publicRead
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Thiếu hoặc file rỗng. Hãy gửi form-data có key='file' type=File"
                ));
            }

            // Upload file lên Drive (My Drive của user Gmail)
            File uploaded = driveService.upload(file);

            // Nếu publicRead = true thì chia sẻ công khai
            if (publicRead) {
                driveService.makeAnyoneReader(uploaded.getId());
            }

            return ResponseEntity.ok(Map.of(
                    "id", uploaded.getId(),
                    "name", uploaded.getName(),
                    "viewLink", uploaded.getWebViewLink(),
                    "downloadLink", uploaded.getWebContentLink()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Kiểm tra kết nối Drive (đọc thông tin user)
     */
    @GetMapping("/test")
    public ResponseEntity<?> testDriveConnection() {
        try {
            String about = driveService.getDriveUserInfo();
            return ResponseEntity.ok(Map.of("info", about));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * API xoá file trên Drive theo id (tuỳ chọn)
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id) {
        try {
            driveService.deleteFile(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá file có ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
