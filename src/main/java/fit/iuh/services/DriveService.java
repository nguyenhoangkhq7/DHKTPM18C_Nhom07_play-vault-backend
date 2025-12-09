package fit.iuh.services;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import fit.iuh.models.DriveLinkUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriveService {

    // Dùng provider để không tạo bean Drive ngay lúc startup
    private final ObjectProvider<Drive> driveProvider;

    @Value("${gdrive.parent-folder-id:}") // optional
    private String parentFolderId;

    /** Lấy Drive khi cần (sẽ ném lỗi nếu chưa /api/drive/auth) */
    private Drive drive() {
        return driveProvider.getObject();
    }

    public File upload(MultipartFile mf) throws IOException {
        if (mf == null || mf.isEmpty()) {
            throw new IllegalArgumentException("File rỗng hoặc thiếu multipart field 'file'.");
        }

        String originalName = Optional.ofNullable(mf.getOriginalFilename())
                .filter(s -> !s.isBlank())
                .orElse("unnamed-upload");

        String contentType = Optional.ofNullable(mf.getContentType())
                .filter(s -> !s.isBlank())
                .orElse("application/octet-stream");

        File meta = new File();
        meta.setName(originalName);
        if (parentFolderId != null && !parentFolderId.isBlank()) {
            meta.setParents(List.of(parentFolderId)); // folder trong My Drive (tuỳ chọn)
        }

        // InputStreamContent không tự đóng stream, nhưng MultipartFile quản lý được; an toàn cho request ngắn.
        InputStreamContent media = new InputStreamContent(contentType, mf.getInputStream());
        if (mf.getSize() > 0) {
            media.setLength(mf.getSize()); // tốt cho Drive (tránh resumable không cần thiết)
        }

        return drive().files()
                .create(meta, media)
                .setSupportsAllDrives(false) // Gmail cá nhân → My Drive
                .setFields("id,name,webViewLink,webContentLink,parents")
                .execute();
    }

    /** Chia sẻ công khai (ai có link cũng xem) */
    public void makeAnyoneReader(String fileId) throws IOException {
        var perm = new com.google.api.services.drive.model.Permission()
                .setType("anyone")
                .setRole("reader");
        drive().permissions()
                .create(fileId, perm)
                .execute();
    }

    /** Lấy thông tin user hiện tại (để test kết nối) */
    public String getDriveUserInfo() throws IOException {
        var about = drive().about().get()
                .setFields("user(displayName,emailAddress)")
                .execute();
        return "Tên: " + about.getUser().getDisplayName()
                + ", Email: " + about.getUser().getEmailAddress();
    }

    /** Xoá file theo ID */
    public void deleteFile(String fileId) throws IOException {
        drive().files().delete(fileId).execute();
    }

//    private final com.google.api.services.drive.Drive drive;

    public String uploadImageAndGetEmbeddableUrl(MultipartFile multipart) throws Exception {
        var meta = new com.google.api.services.drive.model.File()
                .setName(multipart.getOriginalFilename());

        java.io.File temp = java.io.File.createTempFile("upload-", "-" + multipart.getOriginalFilename());
        multipart.transferTo(temp);

        var media = new com.google.api.client.http.FileContent(multipart.getContentType(), temp);
        var uploaded = drive().files().create(meta, media).setFields("id").execute();
        String fileId = uploaded.getId();

        var perm = new com.google.api.services.drive.model.Permission()
                .setType("anyone").setRole("reader");
        drive().permissions().create(fileId, perm).setFields("id").execute();

        temp.delete();

        // URL embeddable dùng trực tiếp trong <img>
        return "https://lh3.googleusercontent.com/d/" + fileId + "=w1200";
    }
}
