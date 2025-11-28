package fit.iuh.controllers;

import fit.iuh.dtos.CustomerDto;
import fit.iuh.services.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@AllArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping()
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }
}
