package fit.iuh.services;

import fit.iuh.dtos.InvoiceHistoryDto;
import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.mappers.InvoiceMapper;
import fit.iuh.models.Invoice;
import fit.iuh.models.enums.InvoiceStatus;
import fit.iuh.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public Page<InvoiceTableDto> getInvoicesForAdmin(String keyword, String statusStr, Pageable pageable) {
        InvoiceStatus status = null;

        // Nếu status khác null, khác rỗng và khác "ALL" thì mới convert sang Enum
        if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = InvoiceStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Nếu client gửi status bậy (VD: "ABC"), coi như là null (tìm tất cả)
                status = null;
            }
        }

        Page<Invoice> page = invoiceRepository.findAllForAdmin(keyword, status, pageable);
        return page.map(invoiceMapper::toTableDto);
    }
}