// fit.iuh.services.ReportService.java
package fit.iuh.services;

import fit.iuh.dtos.ReportRequest;
import fit.iuh.dtos.ReportResponse;
import fit.iuh.models.*;
import fit.iuh.models.enums.ReportStatus;
import fit.iuh.repositories.CustomerRepository;
import fit.iuh.repositories.OrderRepository;
import fit.iuh.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public ReportResponse createReport(ReportRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Lấy customer hiện tại
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        // 2. Kiểm tra đơn hàng tồn tại + thuộc về customer này
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Bạn chỉ có thể báo lỗi cho đơn hàng của mình");
        }

        // 3. Tạo report
        Report report = new Report();
        report.setTitle(request.getTitle());
        report.setDescription(request.getDescription());
        report.setCustomer(customer);
        report.setOrder(order);
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDate.now());

        // 4. Xử lý file đính kèm → vì DB không có cột → nối URL vào description
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            List<String> urls = request.getAttachments().stream()
                    .map(fileStorageService::storeReportAttachment)
                    .collect(Collectors.toList());

            String attachmentText = urls.stream()
                    .map(url -> "\n[Đính kèm] http://localhost:8080" + url)
                    .collect(Collectors.joining());

            report.setDescription(report.getDescription() + attachmentText);
        }

        report = reportRepository.save(report);
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getMyReports() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return reportRepository.findByCustomer_Account_UsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReportResponse toResponse(Report r) {
        ReportResponse resp = new ReportResponse();
        resp.setId(r.getId());
        resp.setTitle(r.getTitle());
        resp.setDescription(r.getDescription());
        resp.setHandlerNote(r.getHandlerNote());
        resp.setCreatedAt(r.getCreatedAt());
        resp.setResolvedAt(r.getResolvedAt());
        resp.setStatus(r.getStatus());
        resp.setOrderId(r.getOrder().getId());
        resp.setOrderCode(String.format("ORD-%03d", r.getOrder().getId()));
        resp.setCustomerId(r.getCustomer().getId());
        resp.setCustomerName(r.getCustomer().getFullName());
        resp.setHandlerUsername(r.getHandlerUsername() != null ? r.getHandlerUsername().getUsername() : null);
        return resp;
    }
}