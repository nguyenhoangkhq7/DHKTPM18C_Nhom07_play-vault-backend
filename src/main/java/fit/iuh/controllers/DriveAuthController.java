package fit.iuh.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory; // cần dependency google-http-client-gson
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
public class DriveAuthController {

    @Value("${gdrive.oauth.client-id}")
    private String clientId;
    @Value("${gdrive.oauth.client-secret}")
    private String clientSecret;
    @Value("${gdrive.oauth.redirect-uri}")
    private String redirectUri;
    @Value("${gdrive.oauth.tokens-file}")
    private String tokensFile;

    private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/drive.file",
            "https://www.googleapis.com/auth/drive.metadata"
    );

    /** B1: Mở trang consent của Google */
    @GetMapping("/api/drive/auth")
    public ResponseEntity<?> authorize() {
        var url = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, SCOPES)
                .setAccessType("offline")        // để nhận refresh_token
                .set("prompt", "consent")        // thêm param prompt=consent
                .build();

        return ResponseEntity.status(302)
                .header("Location", url)
                .build();
    }

    /** B2: Google redirect về đây với ?code=... */
    @GetMapping("/api/drive/oauth2callback")
    public ResponseEntity<?> oauth2callback(@RequestParam("code") String code) throws Exception {
        GoogleTokenResponse tokenResp = new GoogleAuthorizationCodeTokenRequest(
                HTTP_TRANSPORT, JSON_FACTORY,
                "https://oauth2.googleapis.com/token",
                clientId, clientSecret,
                code, redirectUri
        ).execute();

        // Lưu toàn bộ JSON trả về (chứa access_token, refresh_token, …)
        File out = new File(tokensFile);
        if (out.getParentFile() != null) out.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(out, StandardCharsets.UTF_8)) {
            fw.write(tokenResp.toPrettyString());
        }

        return ResponseEntity.ok("✅ Đã nhận token và lưu vào: " + out.getAbsolutePath());
    }
}
