package fit.iuh.mappers;

import fit.iuh.dtos.AdminReportResponse;
import fit.iuh.dtos.ReportResponse;
import fit.iuh.models.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    // Mapping cho User (giữ nguyên)
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "order.id", target = "orderCode")
    ReportResponse toDto(Report report);

    List<ReportResponse> toDtoList(List<Report> reports);

    // --- MỚI: Mapping cho Admin Dashboard ---
    @Mapping(source = "id", target = "reportId")
    @Mapping(source = "order.id", target = "orderCode")
    @Mapping(source = "customer.fullName", target = "customerName")
    @Mapping(source = "customer.account.email", target = "customerEmail")
    @Mapping(source = "order.total", target = "amount")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "status", target = "status")
    @Mapping(source = ".", target = "transactionCode", qualifiedByName = "extractTransactionCode")
    @Mapping(source = "description", target = "description")
    AdminReportResponse toAdminDto(Report report);

//    @Named("formatOrderCode")
//    default String formatOrderCode(Long id) {
//        return id != null ? "ORD-" + id : "N/A";
//    }

    @Named("extractTransactionCode")
    default String extractTransactionCode(Report report) {
        // 1. Ưu tiên: Lấy từ Payment hệ thống (Nếu đơn đã được xử lý/có payment)
        if (report.getOrder() != null && report.getOrder().getPayment() != null) {
            // Nếu bạn có cột transactionCode trong Payment thì lấy nó
            // return report.getOrder().getPayment().getTransactionCode();

            // Hoặc trả về ID payment hệ thống để Admin biết đây là mã nội bộ
            return "SYS_PAY_" + report.getOrder().getPayment().getId();
        }

        // 2. Nếu chưa có Payment (Mất đơn) -> Cố gắng lấy từ Title do User nhập
        String title = report.getTitle();
        if (title != null && title.contains("[Mã GD:")) {
            try {
                // Tìm vị trí bắt đầu và kết thúc của mã trong ngoặc vuông
                int startIndex = title.indexOf("[Mã GD:") + 8; // Bỏ qua 8 ký tự: "[Mã GD: "
                int endIndex = title.indexOf("]", startIndex);

                if (endIndex > startIndex) {
                    // Cắt chuỗi để lấy mã (VD: DG1891)
                    return title.substring(startIndex, endIndex).trim();
                }
            } catch (Exception e) {
                // Phòng trường hợp lỗi format, trả về nguyên title hoặc thông báo lỗi
                return "Lỗi Format Mã";
            }
        }

        // Trường hợp không tìm thấy gì
        return "Chưa có mã";
    }
}