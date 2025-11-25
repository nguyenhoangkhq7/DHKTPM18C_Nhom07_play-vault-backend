package fit.iuh.controllers;

import fit.iuh.dtos.AccountDto;
import fit.iuh.dtos.OrderItemDto;
import fit.iuh.services.AccountService;
import fit.iuh.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class DashboardAdminController {  // Sửa typo: DashBoard → Dashboard
    private final OrderItemService orderItemService;
    private final AccountService accountService;

    @GetMapping("/orderitems/today")
    public ResponseEntity<List<OrderItemDto>> getOrderItemsToday() {
        return ResponseEntity.ok(orderItemService.getItemOrderToday());
    }
    @GetMapping("/accounts/today")
    public ResponseEntity<List<AccountDto>> getAccountToday() {
        return ResponseEntity.ok(accountService.getAccountActiveToday());
    }
}