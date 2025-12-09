package fit.iuh.services;

import fit.iuh.config.CloudflareR2Config;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.http.SdkHttpClient; // <-- THÊM DÒNG NÀY
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;
import software.amazon.awssdk.http.SdkHttpClient;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class R2Service {

    private final CloudflareR2Config config;
    private S3Client s3Client;
    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        System.out.println("🚀 Initializing R2Service...");

        // 1. Bật TLS 1.2+ và disable hostname verification
        System.setProperty("https.protocols", "TLSv1.2,TLSv1.3");
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2,TLSv1.3");

        // 2. Tạo S3Configuration với path-style access
        software.amazon.awssdk.services.s3.S3Configuration serviceConfiguration =
                software.amazon.awssdk.services.s3.S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .chunkedEncodingEnabled(false)
                        .build();

        // 3. Credentials
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                config.accessKey(),
                config.secretKey()
        );

        // 4. Endpoint URL
        URI endpointUri = URI.create("https://" + config.accountId() + ".r2.cloudflarestorage.com");

        // 5. ✅ Tạo HTTP Client (URL Connection thay vì Apache)
        // Sửa kiểu biến thành SdkHttpClient
        software.amazon.awssdk.http.SdkHttpClient httpClient = UrlConnectionHttpClient.builder()
                .build();

        // 6. Tạo S3Client với URL Connection HTTP Client
        s3Client = S3Client.builder()
                .region(Region.of("auto"))
                .endpointOverride(endpointUri)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(serviceConfiguration)
                .httpClient(httpClient) // ✅ QUAN TRỌNG: Dùng URL Connection
                .build();

        // 7. Tạo S3Presigner
        presigner = S3Presigner.builder()
                .region(Region.of("auto"))
                .endpointOverride(endpointUri)
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(serviceConfiguration)
                .build();

        System.out.println("✅ R2Service initialized successfully");
        System.out.println("📦 Bucket: " + config.bucket());
        System.out.println("🔗 Endpoint: " + endpointUri);
        System.out.println("🔧 HTTP Client: UrlConnectionHttpClient");
    }

    /**
     * 🎯 Upload file trực tiếp lên R2
     */
    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(config.bucket())
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            System.out.println("✅ Uploaded: " + fileName + " (" + file.getSize() + " bytes)");

            // Return public URL (nếu bucket public) hoặc presigned URL
            return generateDownloadUrl(fileName);

        } catch (Exception e) {
            System.err.println("❌ Upload error: " + e.getMessage());
            throw new RuntimeException("Failed to upload file: " + fileName, e);
        }
    }

    /**
     * 📤 Generate presigned upload URL (cho frontend upload trực tiếp)
     */
    public String generateUploadUrl(String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(config.bucket())
                    .key(fileName)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String url = presignedRequest.url().toString();

            System.out.println("📤 Generated upload URL for: " + fileName);
            return url;

        } catch (Exception e) {
            System.err.println("❌ Error generating upload URL: " + e.getMessage());
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    /**
     * 📥 Generate presigned download URL
     */
    public String generateDownloadUrl(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(config.bucket())
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(30)) // URL valid trong 1 giờ
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();

            System.out.println("📥 Generated download URL for: " + fileName);
            return url;

        } catch (Exception e) {
            System.err.println("❌ Error generating download URL: " + e.getMessage());
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    /**
     * 📋 List tất cả files trong bucket
     */
    public List<FileInfo> listFiles() {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(config.bucket())
                    .maxKeys(100)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listRequest);

            List<FileInfo> files = response.contents().stream()
                    .map(s3Object -> new FileInfo(
                            s3Object.key(),
                            s3Object.size(),
                            s3Object.lastModified().toString()
                    ))
                    .collect(Collectors.toList());

            System.out.println("📋 Listed " + files.size() + " files");
            return files;

        } catch (Exception e) {
            System.err.println("❌ List files error: " + e.getMessage());
            throw new RuntimeException("Failed to list files", e);
        }
    }

    /**
     * 🗑️ Delete file
     */
    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(config.bucket())
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("🗑️ Deleted: " + fileName);

        } catch (Exception e) {
            System.err.println("❌ Delete error: " + e.getMessage());
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    /**
     * DTO cho file info
     */
    public record FileInfo(String key, Long size, String lastModified) {}
}