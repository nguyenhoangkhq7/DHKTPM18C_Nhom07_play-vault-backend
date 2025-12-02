package fit.iuh.services.impl;

import fit.iuh.dtos.AdminReportResponse;
import fit.iuh.dtos.CreateReportRequest;
import fit.iuh.dtos.ProcessReportRequest;
import fit.iuh.dtos.ReportResponse;
import fit.iuh.mappers.ReportMapper;
import fit.iuh.models.*;
import fit.iuh.models.enums.*;
import fit.iuh.repositories.*;
import fit.iuh.services.EmailService;
import fit.iuh.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final ReportMapper reportMapper;
    private final EmailService emailService;

    // --- ADMIN: Lấy danh sách hiển thị bảng ---
    @Override
    public Page<AdminReportResponse> getReportsForAdmin(Pageable pageable) {
        return reportRepository.findAll(pageable)
                .map(reportMapper::toAdminDto);
    }

    // --- ADMIN: Xử lý sự cố (Core Logic) ---
    @Override
    @Transactional
    public ReportResponse processReport(Long reportId, String adminUsername, ProcessReportRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo cáo"));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("Báo cáo này đã được xử lý rồi!");
        }

        // 1. Lưu thông tin người xử lý
        Account admin = accountRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));
        report.setHandlerUsername(admin);
        report.setHandlerNote(request.getAdminNote());
        report.setResolvedAt(LocalDate.now());

        // 2. Logic Duyệt hoặc Từ chối
        if (request.getApproved()) {
            // === CHẤP THUẬN (Tiền đã về) ===
            report.setStatus(ReportStatus.RESOLVED);

            Order order = report.getOrder();
            Payment payment = order.getPayment();

            // A. Xử lý Payment (Nếu chưa có thì tạo mới)
            if (payment == null) {
                payment = new Payment();
                payment.setAmount(order.getTotal());
                payment.setPaymentDate(LocalDate.now());
                payment.setPaymentMethod(PaymentMethod.ZALOPAY); // Mặc định hoặc lấy từ user input
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setInvoice(null);

                payment = paymentRepository.save(payment);
                order.setPayment(payment); // Link vào Order
            } else {
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
            }

            // B. Xử lý Order & Library (Nếu chưa hoàn tất)
            if (order.getStatus() != OrderStatus.COMPLETED) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);

                // C. Cộng game vào thư viện
                Customer customer = order.getCustomer();
                boolean isLibraryUpdated = false;
                for (OrderItem item : order.getOrderItems()) {
                    Game game = item.getGame();
                    if (!customer.getLibrary().contains(game)) {
                        customer.getLibrary().add(game);
                        isLibraryUpdated = true;
                    }
                }
                if (isLibraryUpdated) {
                    customerRepository.save(customer);
                }
            }

        } else {
            // === TỪ CHỐI (Không thấy tiền) ===
            report.setStatus(ReportStatus.REJECT);
            // Không làm gì với Order/Payment cả
        }

        Report savedReport = reportRepository.save(report);
        try {
            String toEmail = report.getCustomer().getAccount().getEmail();
            String username = report.getCustomer().getFullName();

            emailService.sendReportResultEmail(
                    toEmail,
                    username,
                    report.getId(),
                    report.getTitle(),
                    request.getApproved(), // true/false
                    request.getAdminNote() // Ghi chú của admin
            );
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để tránh rollback transaction
            System.err.println("Lỗi gửi email báo cáo: " + e.getMessage());
        }

        return reportMapper.toDto(savedReport);
    }


    @Override
    @Transactional
    public ReportResponse createReport(String username, CreateReportRequest request) {
        // 1. Tìm Customer
        Customer customer = customerRepository.findByAccount_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng"));

        // 2. Tìm Order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // 3. Validation: Đơn hàng phải chính chủ
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Bạn không có quyền báo cáo đơn hàng này");
        }

        // 4. Validation: Không cho báo cáo đơn đã hoàn thành
        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Đơn hàng này đã hoàn tất, không thể báo cáo lỗi thanh toán.");
        }

        // 5. Tạo Report
        Report report = new Report();

        // --- LOGIC QUAN TRỌNG: Lưu Mã GD vào Title ---
        // Format: "[Mã GD: CODE] Tiêu đề người dùng nhập"
        String formattedTitle = String.format("[Mã GD: %s] %s",
                request.getTransactionCode().trim().toUpperCase(),
                request.getTitle());

        report.setTitle(formattedTitle);
        report.setDescription(request.getDescription());
        // ---------------------------------------------

        report.setCreatedAt(LocalDate.now());
        report.setStatus(ReportStatus.PENDING);
        report.setCustomer(customer);
        report.setOrder(order);

        Report savedReport = reportRepository.save(report);
        return reportMapper.toDto(savedReport);
    }

    @Override
    public List<ReportResponse> getMyReports(String username) {
        return reportMapper.toDtoList(
                reportRepository.findByCustomer_Account_UsernameOrderByCreatedAtDesc(username)
        );
    }
}