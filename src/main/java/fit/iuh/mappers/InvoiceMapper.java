package fit.iuh.mappers;

import fit.iuh.dtos.InvoiceHistoryDto;
import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.models.Invoice;
import fit.iuh.models.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    List<InvoiceHistoryDto> toHistoryDtoList(List<Invoice> invoices);

    // Helper: Tạo mã hóa đơn INV-001
    @Named("formatInvoiceCode")
    default String formatInvoiceCode(Long id) {
        return id != null ? String.format("INV-%05d", id) : "N/A";
    }

    // Helper: Tạo mã đơn hàng ORD-001
    @Named("formatOrderCode")
    default String formatOrderCode(Long id) {
        return id != null ? String.format("ORD-%03d", id) : "N/A";
    }

    // Helper: Lấy danh sách tên game từ OrderItems
    @Named("mapGameTitles")
    default List<String> mapGameTitles(List<OrderItem> items) {
        if (items == null || items.isEmpty()) return Collections.emptyList();

        return items.stream()
                .map(item -> item.getGame().getGameBasicInfos().getName())
                .collect(Collectors.toList());
    }

    // Thêm vào InvoiceMapper.java

    @Mapping(source = "id", target = "invoiceCode", qualifiedByName = "formatInvoiceCode")
    @Mapping(source = "customer.fullName", target = "customerName")
      InvoiceTableDto toTableDto(Invoice invoice);
}