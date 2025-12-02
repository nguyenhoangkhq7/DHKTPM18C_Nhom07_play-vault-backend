package fit.iuh.services;

import fit.iuh.dtos.AdminReportResponse;
import fit.iuh.dtos.CreateReportRequest;
import fit.iuh.dtos.ProcessReportRequest;
import fit.iuh.dtos.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportService {
    // Admin: Lấy danh sách báo cáo (có phân trang)
    Page<AdminReportResponse> getReportsForAdmin(Pageable pageable);

    // Admin: Xử lý báo cáo
    ReportResponse processReport(Long reportId, String adminUsername, ProcessReportRequest request);

    // ... các method của Customer (createReport, getMyReports) giữ nguyên
    ReportResponse createReport(String username, CreateReportRequest request);
    List<ReportResponse> getMyReports(String username);
}