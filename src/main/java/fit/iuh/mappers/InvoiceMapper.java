package fit.iuh.mappers;

import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.models.Invoice;
import fit.iuh.models.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "id", target = "invoiceCode", qualifiedByName = "formatInvoiceCode")
    @Mapping(source = "customer.fullName", target = "customerName")

    // --- LẤY EMAIL TỪ ACCOUNT ---
    @Mapping(source = "customer.account.email", target = "email")

    // Lấy Payment Method từ list payments
    @Mapping(source = "payments", target = "paymentMethod", qualifiedByName = "mapPaymentMethod")
    InvoiceTableDto toTableDto(Invoice invoice);

    @Named("formatInvoiceCode")
    default String formatInvoiceCode(Long id) {
        return id != null ? String.format("INV-%05d", id) : "N/A";
    }

    @Named("mapPaymentMethod")
    default String mapPaymentMethod(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return null;
        // Lấy phương thức của payment đầu tiên
        return payments.get(0).getPaymentMethod() != null
                ? payments.get(0).getPaymentMethod().toString()
                : null;
    }
}