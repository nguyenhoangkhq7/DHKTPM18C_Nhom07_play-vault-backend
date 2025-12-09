package fit.iuh.controllers;

import fit.iuh.dtos.InvoiceTableDto;
import fit.iuh.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

@RestController
@RequestMapping("/api/admin/invoices")
@RequiredArgsConstructor
public class AdminInvoiceController {

    private final InvoiceService invoiceService;

    // GET /api/admin/invoices?page=0&size=10&keyword=Nguyen
    @GetMapping
    public ResponseEntity<Page<InvoiceTableDto>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status)
            {
        Pageable pageable = PageRequest.of(page, size);
        Page<InvoiceTableDto> result = invoiceService.getInvoicesForAdmin(keyword, status,pageable);
        return ResponseEntity.ok(result);
    }
}