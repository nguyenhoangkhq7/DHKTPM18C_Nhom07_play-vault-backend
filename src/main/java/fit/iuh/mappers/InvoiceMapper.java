package fit.iuh.mappers;

import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.models.Invoice;
import fit.iuh.models.Payment;
import fit.iuh.models.enums.PaymentStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "id", target = "invoiceCode", qualifiedByName = "formatInvoiceCode")
    @Mapping(source = "customer.fullName", target = "customerName")

    // 1. Lấy Email từ Account thông qua Customer
    @Mapping(source = "customer.account.email", target = "email")

    // 2. Xử lý logic lấy Payment Method thành công
    @Mapping(source = "payments", target = "paymentMethod", qualifiedByName = "mapSuccessfulPaymentMethod")
    InvoiceTableDto toTableDto(Invoice invoice);

    @Named("formatInvoiceCode")
    default String formatInvoiceCode(Long id) {
        return id != null ? String.format("INV-%05d", id) : "N/A";
    }

    @Named("mapSuccessfulPaymentMethod")
    default String mapSuccessfulPaymentMethod(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            return null;
        }
        // Chỉ lấy phương thức của giao dịch có trạng thái SUCCESS
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .findFirst()
                .map(p -> p.getPaymentMethod().toString())
                .orElse(null);
    }
}