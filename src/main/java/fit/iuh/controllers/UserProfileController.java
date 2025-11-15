package fit.iuh.controllers;

import fit.iuh.dtos.OrderDto;
import fit.iuh.dtos.UserProfileDto;
import fit.iuh.dtos.UpdateAccountRequest;
import fit.iuh.dtos.UpdateProfileRequest;
import fit.iuh.services.UserProfileService;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import fit.iuh.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileDto> getProfile(@PathVariable Long id) {
        UserProfileDto dto = userProfileService.getProfile(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<Page<OrderDto>> getOrders(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderDto> orders = userProfileService.getOrderHistory(id, PageRequest.of(page, size));
        return ResponseEntity.ok(orders);
    }

    // Update profile (uses UpdateProfileRequest)
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        // simple implementation: load customer, update fields and save
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (request.getFullName() != null) c.setFullName(request.getFullName());
        if (request.getDateOfBirth() != null) c.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
        if (request.getAvatarUrl() != null) c.setAvatarUrl(request.getAvatarUrl());
        customerRepository.save(c);

        UserProfileDto updated = userProfileService.getProfile(id);
        return ResponseEntity.ok(updated);
    }

    // Update account (email/phone) — request validated
    @PutMapping("/{id}/account")
    public ResponseEntity<UserProfileDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Account account = c.getAccount();
        if (account == null) throw new RuntimeException("Customer has no account");

        if (request.getEmail() != null) account.setEmail(request.getEmail());
        if (request.getPhone() != null) account.setPhone(request.getPhone());

        accountRepository.save(account);

        UserProfileDto updated = userProfileService.getProfile(id);
        return ResponseEntity.ok(updated);
    }
}
