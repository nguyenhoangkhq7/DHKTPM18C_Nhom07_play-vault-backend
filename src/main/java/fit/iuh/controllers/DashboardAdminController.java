package fit.iuh.controllers;

import fit.iuh.dtos.AccountDto;
import fit.iuh.dtos.GameDto;
import fit.iuh.services.AccountService;
import fit.iuh.services.GameService;
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
    private final GameService gameService;
    private final AccountService accountService;

    @GetMapping("/orderitems/today")
    public ResponseEntity<List<GameDto>> getOrderItemsToday() {
        return ResponseEntity.ok(gameService.getAllByGameToday());
    }
    @GetMapping("/accounts/today")
    public ResponseEntity<List<AccountDto>> getAccountToday() {
        return ResponseEntity.ok(accountService.getAccountActiveToday());
    }
}