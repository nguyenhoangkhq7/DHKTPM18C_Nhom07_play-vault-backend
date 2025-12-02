package fit.iuh.controllers;

import fit.iuh.dtos.AdminReportResponse;
import fit.iuh.dtos.CreateReportRequest;
import fit.iuh.dtos.ProcessReportRequest;
import fit.iuh.dtos.ReportResponse;
import fit.iuh.services.UserReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports") // URL dành riêng cho Admin
@RequiredArgsConstructor
public class AdminReportController {

    private final UserReportService reportService;

    // 1. Lấy danh sách báo cáo (Dashboard)
    // GET /api/admin/reports?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<AdminReportResponse>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Sắp xếp mặc định: Mới nhất lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(reportService.getReportsForAdmin(pageable));
    }

    // 2. Xử lý báo cáo (Duyệt/Từ chối)
    // PUT /api/admin/reports/{id}/process
    @PutMapping("/{id}/process")
    public ResponseEntity<ReportResponse> processReport(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProcessReportRequest request) {

        if (authentication == null) return ResponseEntity.status(401).build();

        // TODO: Kiểm tra role ADMIN nếu chưa cấu hình SecurityConfig
        // if (!authentication.getAuthorities().contains(...)) ...

        return ResponseEntity.ok(reportService.processReport(
                id,
                authentication.getName(),
                request
        ));
    }


    // 1. CUSTOMER: Gửi báo cáo sự cố
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            Authentication authentication,
            @Valid @RequestBody CreateReportRequest request) {

        if (authentication == null) return ResponseEntity.status(401).build();

        ReportResponse response = reportService.createReport(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    // 2. CUSTOMER: Xem lịch sử báo cáo của mình
    @GetMapping("/my-reports")
    public ResponseEntity<List<ReportResponse>> getMyReports(Authentication authentication) {
        if (authentication == null) return ResponseEntity.status(401).build();

        List<ReportResponse> list = reportService.getMyReports(authentication.getName());
        return ResponseEntity.ok(list);
    }
}