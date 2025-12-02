package fit.iuh.controllers;

import fit.iuh.dtos.CartResponse;
import fit.iuh.dtos.CheckoutRequestDto;
import fit.iuh.dtos.GameDetailDto;
import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.services.CheckoutService;
import fit.iuh.services.JwtService;
import fit.iuh.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private JwtService jwtService;

    // GET: Lấy lịch sử mua hàng
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String username = jwtService.getUsernameFromToken(token);

        List<OrderHistoryResponse> history = orderService.getOrderHistory(username);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/checkout/selected")
    public ResponseEntity<CartResponse> checkoutSelected(
            @RequestBody (required = false) CheckoutRequestDto requestDto,
            Principal principal
    ) {
        if (requestDto == null || requestDto.getItemIds() == null || requestDto.getItemIds().isEmpty()) {
            // Nếu DTO bị null, Spring đã không thể đọc body
            return ResponseEntity.status(400).body(null);
        }

        String username = principal.getName();
        // Gọi Service, truyền danh sách ID từ DTO
        CartResponse updatedCart = checkoutService.checkoutSelectedItems(username, requestDto.getItemIds());
        return ResponseEntity.ok(updatedCart);
    }

    // ========================================================================
    // POST: Thanh toán toàn bộ giỏ hàng (CHECKOUT ALL)
    // ========================================================================
    @PostMapping("/checkout/all")
    public ResponseEntity<CartResponse> checkoutAll(Principal principal) {
        String username = principal.getName();
        // Logic tìm tất cả item ID được xử lý bên trong CheckoutService
        CartResponse updatedCart = checkoutService.checkoutAllItems(username);
        return ResponseEntity.ok(updatedCart);
    }


    /**
     * API Kiểm tra đơn hàng có tồn tại và thuộc về User đang đăng nhập hay không.
     * URL: GET http://localhost:8080/api/orders/{id}/validate
     * Return: true (nếu đúng chính chủ), false (nếu sai hoặc không tồn tại)
     */
    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateOrderOwnership(
            @PathVariable Long id,
            Principal principal) {

        // 1. Nếu chưa đăng nhập -> auto false
        if (principal == null) {
            return ResponseEntity.ok(false);
        }

        // 2. Gọi Service kiểm tra (đã viết ở bước trước)
        boolean isValid = orderService.checkIsOwnOrder(id, principal.getName());

        // 3. Trả về true/false (HTTP 200)
        return ResponseEntity.ok(isValid);
    }

}