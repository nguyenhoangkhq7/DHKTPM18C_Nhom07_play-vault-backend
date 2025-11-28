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
    @Lazy  // tránh crash khi chưa cấp quyền
    public Drive googleDrive() throws Exception {
        // Đọc JSON token từ file (file bạn vừa gửi nội dung)
        String json = Files.readString(Path.of(tokensFile));
        var obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();

        if (!obj.has("refresh_token")) {
            throw new IllegalStateException("Token file không có refresh_token. Hãy /api/drive/auth lại (offline + prompt=consent).");
        }
        String refreshToken = obj.get("refresh_token").getAsString();

        var http = new NetHttpTransport();
        var jf   = GsonFactory.getDefaultInstance();

        // Dùng GoogleCredential với refresh_token (tự refresh access token khi cần)
        var cred = new GoogleCredential.Builder()
                .setTransport(http)
                .setJsonFactory(jf)
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setRefreshToken(refreshToken);

        // Thử refresh ngay để kiểm tra refresh_token (nếu fail -> invalid_grant)
        if (!cred.refreshToken()) {
            throw new IllegalStateException("refresh_token không hợp lệ hoặc hết hạn. Hãy revoke & /api/drive/auth lại.");
        }

        return new Drive.Builder(http, jf, cred)
                .setApplicationName("play-vault-backend")
                .build();
    }
}
