package fit.iuh.controllers;

import fit.iuh.dtos.InvoiceHistoryDto;
import fit.iuh.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // API: GET /api/invoices/history
    @GetMapping("/history")
    public ResponseEntity<List<InvoiceHistoryDto>> getInvoiceHistory(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String username = authentication.getName();
        List<InvoiceHistoryDto> history = invoiceService.getMyInvoices(username);

        return ResponseEntity.ok(history);
    }
}