// src/main/java/fit/iuh/services/FileStorageService.java
package fit.iuh.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:/uploads/reports}")
    private String uploadDir;

    public String storeReportAttachment(MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + "_" + System.currentTimeMillis() + ext;
            Path target = root.resolve(filename);
            Files.copy(file.getInputStream(), target);
            return "/uploads/reports/" + filename; // URL truy cập được từ frontend
        } catch (IOException e) {
            throw new RuntimeException("Lưu file thất bại: " + e.getMessage());
        }
    }
}