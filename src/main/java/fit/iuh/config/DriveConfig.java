package fit.iuh.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class DriveConfig {

    @Value("${gdrive.oauth.client-id}")
    String clientId;

    @Value("${gdrive.oauth.client-secret}")
    String clientSecret;

    @Value("${gdrive.oauth.tokens-file}")
    String tokensFile;

    @Bean
    @Lazy
    public Drive googleDrive() {
        var http = new NetHttpTransport();
        var jf = GsonFactory.getDefaultInstance();
        String refreshToken = null;

        // --- ĐOẠN SỬA QUAN TRỌNG: Kiểm tra file tồn tại trước khi đọc ---
        try {
            Path path = Path.of(tokensFile);
            // Chỉ đọc nếu file thực sự tồn tại
            if (Files.exists(path)) {
                String json = Files.readString(path);
                var obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                if (obj.has("refresh_token")) {
                    refreshToken = obj.get("refresh_token").getAsString();
                }
            }
        } catch (Exception e) {
            // Nếu lỗi đọc file, chỉ in ra console chứ KHÔNG làm sập app
            System.err.println("⚠️ Cảnh báo: Không đọc được file token cũ (" + e.getMessage() + ")");
        }

        // Tạo đối tượng Credential
        GoogleCredential.Builder credBuilder = new GoogleCredential.Builder()
                .setTransport(http)
                .setJsonFactory(jf)
                .setClientSecrets(clientId, clientSecret);

        GoogleCredential cred = credBuilder.build();

        // Nếu tìm thấy token cũ thì set vào, nếu không thì thôi (vẫn chạy tiếp)
        if (refreshToken != null) {
            cred.setRefreshToken(refreshToken);
            try {
                if (!cred.refreshToken()) {
                    System.err.println("⚠️ Token cũ đã hết hạn. Hãy đăng nhập lại.");
                } else {
                    System.out.println("✅ Google Drive kết nối thành công!");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi kiểm tra token: " + e.getMessage());
            }
        } else {
            System.out.println("ℹ️ Chưa có Token. Ứng dụng sẽ khởi động ở chế độ chờ đăng nhập.");
        }

        // Luôn trả về Bean Drive để Spring Boot khởi động thành công
        return new Drive.Builder(http, jf, cred)
                .setApplicationName("play-vault-backend")
                .build();
    }
}