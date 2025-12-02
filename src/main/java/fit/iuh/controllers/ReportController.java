// fit.iuh.controllers.ReportController.java
package fit.iuh.controllers;

import fit.iuh.dtos.OrderForReportDto;
import fit.iuh.dtos.ReportRequest;
import fit.iuh.dtos.ReportResponse;
import fit.iuh.models.Order;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // Customer gửi báo lỗi (bắt buộc chọn đơn hàng)
    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @ModelAttribute ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    // Xem báo cáo của mình
    @GetMapping("/my")
    public ResponseEntity<List<ReportResponse>> myReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }

    // Admin xem tất cả
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportResponse>> allReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

}