package fit.iuh.services;

import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.models.enums.InvoiceStatus;
import fit.iuh.repositories.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public Page<InvoiceTableDto> getInvoicesForAdmin(String keyword, String statusStr, Pageable pageable) {
        InvoiceStatus status = null;
        if (statusStr != null && !statusStr.isBlank() && !"ALL".equalsIgnoreCase(statusStr)) {
            try {
                status = InvoiceStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                status = null;
            }
        }

        // Gọi hàm Custom Query mới
        return invoiceRepository.findAllForAdminCustom(keyword, status, pageable);
    }
}