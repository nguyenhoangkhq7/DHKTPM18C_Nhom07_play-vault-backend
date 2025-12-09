// fit/iuh/controllers/SystemCompatibilityController.java
package fit.iuh.controllers;

import fit.iuh.dtos.SystemInfoResponse;
import fit.iuh.services.DXDiagParserService;
import fit.iuh.services.ConfigComparisonService;
import fit.iuh.models.SystemInfo;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.Customer;
import fit.iuh.models.SystemRequirement;
import fit.iuh.repositories.SystemInfoRepository;
import fit.iuh.repositories.GameBasicInfoRepository;
import fit.iuh.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system-config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SystemCompatibilityController {

    private final DXDiagParserService dxDiagParserService;
    private final ConfigComparisonService configComparisonService;
    private final SystemInfoRepository systemInfoRepository;
    private final GameBasicInfoRepository gameBasicInfoRepository;
    private final CustomerRepository customerRepository;

    /**
     * Upload và phân tích file DXDiag
     */
    @PostMapping("/upload")
    public ResponseEntity<SystemInfoResponse> uploadDXDiag(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        try {
            // Lấy Customer từ User
            Customer customer = customerRepository.findByAccount_Username(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại"));

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(SystemInfoResponse.builder()
                                .success(false)
                                .message("File không được để trống")
                                .build());
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                return ResponseEntity.badRequest()
                        .body(SystemInfoResponse.builder()
                                .success(false)
                                .message("Chỉ chấp nhận file .txt")
                                .build());
            }

            // Parse file content
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Map<String, Object> parsedData = dxDiagParserService.parseDXDiagFile(content);

            // Save to database
            SystemInfo systemInfo = systemInfoRepository.findByCustomer(customer)
                    .orElse(new SystemInfo());

            systemInfo.setCustomer(customer);
            systemInfo.setOs((String) parsedData.get("os"));
            systemInfo.setCpu((String) parsedData.get("processor"));
            systemInfo.setGpu((String) parsedData.get("gpu"));

            Integer ramGB = (Integer) parsedData.get("ram");
            systemInfo.setRam(ramGB + " GB");

            systemInfo.setDirectxVersion((String) parsedData.get("directX"));
            systemInfo.setDxdiagContent(content);

            systemInfoRepository.save(systemInfo);

            // Build response
            SystemInfoResponse.SystemInfoDTO systemInfoDTO = SystemInfoResponse.SystemInfoDTO.builder()
                    .id(systemInfo.getId())
                    .os(systemInfo.getOs())
                    .cpu(systemInfo.getCpu())
                    .gpu(systemInfo.getGpu())
                    .ram(systemInfo.getRam())
                    .directxVersion(systemInfo.getDirectxVersion())
                    .lastUpdated(systemInfo.getLastUpdated())
                    .build();

            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(true)
                    .message("Đã phân tích và lưu cấu hình thành công")
                    .systemInfo(systemInfoDTO)
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message("Lỗi đọc file: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message("Lỗi xử lý file: " + e.getMessage())
                            .build());
        }
    }

    /**
     * So sánh cấu hình với game cụ thể
     */
    @GetMapping("/compare/{gameId}")
    public ResponseEntity<SystemInfoResponse> compareWithGame(
            @PathVariable Long gameId,
            @AuthenticationPrincipal User user) {

        try {
            // Lấy Customer từ User
            Customer customer = customerRepository.findByAccount_Username(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại"));

            // Get user's system info
            SystemInfo systemInfo = systemInfoRepository.findByCustomer(customer)
                    .orElseThrow(() -> new RuntimeException("Bạn chưa upload cấu hình hệ thống. Vui lòng upload file DXDiag.txt trước."));

            // Get game requirements
            GameBasicInfo game = gameBasicInfoRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game không tồn tại"));

            SystemRequirement systemRequirement = game.getSystemRequirement();
            if (systemRequirement == null) {
                return ResponseEntity.ok(SystemInfoResponse.builder()
                        .success(false)
                        .message("Game này chưa có thông tin cấu hình yêu cầu")
                        .build());
            }
            // Prepare user system map
            Map<String, Object> userSystem = new HashMap<>();
            userSystem.put("os", systemInfo.getOs());
            userSystem.put("cpu", systemInfo.getCpu());
            userSystem.put("gpu", systemInfo.getGpu());
            userSystem.put("ram", systemInfo.getRam());


            // Prepare game requirements map
            Map<String, Object> gameRequirements = new HashMap<>();
            gameRequirements.put("os", systemRequirement.getOs() != null ?
                    systemRequirement.getOs().toString() : "Windows 10");
            gameRequirements.put("cpu", systemRequirement.getCpu());
            gameRequirements.put("gpu", systemRequirement.getGpu());
            gameRequirements.put("ram", systemRequirement.getRam());
            gameRequirements.put("name", game.getName());

            // Compare configurations
            Map<String, Object> comparisonResult = configComparisonService
                    .compareConfiguration(userSystem, gameRequirements);

            // Check if comparison was successful
            if (!(Boolean) comparisonResult.getOrDefault("success", false)) {
                return ResponseEntity.ok(SystemInfoResponse.builder()
                        .success(false)
                        .message((String) comparisonResult.get("error"))
                        .build());
            }

            // Build response
            SystemInfoResponse.CompatibilityResult compatibilityResult =
                    SystemInfoResponse.CompatibilityResult.builder()
                            .score((Double) comparisonResult.get("score"))
                            .level((String) comparisonResult.get("level"))
                            .percentage((Integer) comparisonResult.get("percentage"))
                            .details((Map<String, Double>) comparisonResult.get("detdatabase.sqlails"))
                            .userSystem(convertToSystemSpecs(
                                    (Map<String, Object>) comparisonResult.get("userSystem")))
                            .requiredSystem(convertToSystemSpecs(
                                    (Map<String, Object>) comparisonResult.get("requiredSystem")))
                            .recommendations((java.util.List<String>) comparisonResult.get("recommendations"))
                            .build();

            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(true)
                    .compatibilityResult(compatibilityResult)
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message("Lỗi khi so sánh cấu hình: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Lấy thông tin cấu hình đã lưu của user
     */
    @GetMapping("/user")
    public ResponseEntity<SystemInfoResponse> getUserSystemInfo(
            @AuthenticationPrincipal User user) {

        try {
            Customer customer = customerRepository.findByAccount_Username(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại"));

            SystemInfo systemInfo = systemInfoRepository.findByCustomer(customer)
                    .orElseThrow(() -> new RuntimeException("Bạn chưa upload cấu hình hệ thống"));

            SystemInfoResponse.SystemInfoDTO systemInfoDTO = SystemInfoResponse.SystemInfoDTO.builder()
                    .id(systemInfo.getId())
                    .os(systemInfo.getOs())
                    .cpu(systemInfo.getCpu())
                    .gpu(systemInfo.getGpu())
                    .ram(systemInfo.getRam())
                    .directxVersion(systemInfo.getDirectxVersion())
                    .lastUpdated(systemInfo.getLastUpdated())
                    .build();

            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(true)
                    .systemInfo(systemInfoDTO)
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * API để kiểm tra nhanh mà không cần đăng nhập
     */
    @PostMapping("/quick-check/{gameId}")
    public ResponseEntity<SystemInfoResponse> quickCheck(
            @PathVariable Long gameId,
            @RequestParam("file") MultipartFile file) {

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(SystemInfoResponse.builder()
                                .success(false)
                                .message("File không được để trống")
                                .build());
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                return ResponseEntity.badRequest()
                        .body(SystemInfoResponse.builder()
                                .success(false)
                                .message("Chỉ chấp nhận file .txt")
                                .build());
            }

            // Parse file
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            Map<String, Object> parsedData = dxDiagParserService.parseDXDiagFile(content);
            if (parsedData.containsKey("error")) {
                return ResponseEntity.badRequest().body(
                        SystemInfoResponse.builder()
                                .success(false)
                                .message("File không hợp lệ! Vui lòng chạy dxdiag.exe → Save All Information → chọn file .txt đó.")
                                .build()
                );
            }
            // Get game requirements
            GameBasicInfo game = gameBasicInfoRepository.findById(gameId)
                    .orElseThrow(() -> new RuntimeException("Game không tồn tại"));

            SystemRequirement systemRequirement = game.getSystemRequirement();
            if (systemRequirement == null) {
                return ResponseEntity.ok(SystemInfoResponse.builder()
                        .success(false)
                        .message("Game này chưa có thông tin cấu hình yêu cầu")
                        .build());
            }

            // Prepare user system map
            Map<String, Object> userSystem = new HashMap<>();
            userSystem.put("os", parsedData.get("os"));
            userSystem.put("cpu", parsedData.get("processor"));
            userSystem.put("gpu", parsedData.get("gpu"));

            Integer ramGB = (Integer) parsedData.get("ram");
            userSystem.put("ram", ramGB + " GB");

            // Prepare game requirements map
            Map<String, Object> gameRequirements = new HashMap<>();
            gameRequirements.put("os", systemRequirement.getOs() != null ?
                    systemRequirement.getOs().toString() : "Windows 10");
            gameRequirements.put("cpu", systemRequirement.getCpu());
            gameRequirements.put("gpu", systemRequirement.getGpu());
            gameRequirements.put("ram", systemRequirement.getRam());

            // Compare
            Map<String, Object> comparisonResult = configComparisonService
                    .compareConfiguration(userSystem, gameRequirements);

            // Check if comparison was successful
            if (!(Boolean) comparisonResult.getOrDefault("success", false)) {
                return ResponseEntity.ok(SystemInfoResponse.builder()
                        .success(false)
                        .message((String) comparisonResult.get("error"))
                        .build());
            }

            // Prepare user specs for response
            Map<String, Object> userSpecs = new HashMap<>();
            userSpecs.put("os", parsedData.get("os"));
            userSpecs.put("cpu", parsedData.get("processor"));
            userSpecs.put("gpu", parsedData.get("gpu"));
            userSpecs.put("ram", ramGB + " GB");

            // Build response
            SystemInfoResponse.CompatibilityResult compatibilityResult =
                    SystemInfoResponse.CompatibilityResult.builder()
                            .score((Double) comparisonResult.get("score"))
                            .level((String) comparisonResult.get("level"))
                            .percentage((Integer) comparisonResult.get("percentage"))
                            .details((Map<String, Double>) comparisonResult.get("details"))
                            .userSystem(convertToSystemSpecs(userSpecs))
                            .requiredSystem(convertToSystemSpecs(gameRequirements))
                            .recommendations((java.util.List<String>) comparisonResult.get("recommendations"))
                            .build();

            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(true)
                    .compatibilityResult(compatibilityResult)
                    .build());

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message("Lỗi đọc file: " + e.getMessage())
                            .build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message("Lỗi: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Xóa cấu hình của user
     */
    @DeleteMapping("/delete")
    public ResponseEntity<SystemInfoResponse> deleteSystemInfo(
            @AuthenticationPrincipal User user) {

        try {
            Customer customer = customerRepository.findByAccount_Username(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("Customer không tồn tại"));

            if (!systemInfoRepository.existsByCustomer(customer)) {
                return ResponseEntity.ok(SystemInfoResponse.builder()
                        .success(false)
                        .message("Bạn chưa có cấu hình nào được lưu")
                        .build());
            }

            systemInfoRepository.deleteByCustomer(customer);

            return ResponseEntity.ok(SystemInfoResponse.builder()
                    .success(true)
                    .message("Đã xóa cấu hình hệ thống")
                    .build());

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(SystemInfoResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    private SystemInfoResponse.SystemSpecs convertToSystemSpecs(Map<String, Object> data) {
        if (data == null) return null;

        return SystemInfoResponse.SystemSpecs.builder()
                .os((String) data.get("os"))
                .cpu((String) data.get("cpu"))
                .gpu((String) data.get("gpu"))
                .ram((String) data.get("ram"))
                .build();
    }
}