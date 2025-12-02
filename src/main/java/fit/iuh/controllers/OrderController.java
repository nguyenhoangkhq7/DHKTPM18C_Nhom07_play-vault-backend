package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.services.CheckoutService;
import fit.iuh.services.JwtService;
import fit.iuh.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private JwtService jwtService;

    // GET: Lấy lịch sử đơn hàng
    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "").trim();
        String username = jwtService.getUsernameFromToken(token);

        List<OrderHistoryResponse> history = orderService.getOrderHistory(username);
        return ResponseEntity.ok(history);
    }

    // POST: Thanh toán các mục đã chọn
    @PostMapping("/checkout/selected")
    public ResponseEntity<CheckoutResponseDto> checkoutSelected(
            @RequestBody(required = false) CheckoutRequestDto requestDto,
            Principal principal) {

        // Validate request body
        if (requestDto == null || requestDto.getItemIds() == null || requestDto.getItemIds().isEmpty()) {
            CheckoutResponseDto error = new CheckoutResponseDto(
                    false,
                    "Vui lòng chọn ít nhất một sản phẩm để thanh toán",
                    0L,
                    null
            );
            return ResponseEntity.badRequest().body(error);
        }

        String username = principal.getName();
        CheckoutResponseDto response = checkoutService.checkoutSelectedItems(username, requestDto.getItemIds());

        return ResponseEntity.ok(response);
    }

    // POST: Thanh toán toàn bộ giỏ hàng
    @PostMapping("/checkout/all")
    public ResponseEntity<CheckoutResponseDto> checkoutAll(Principal principal) {
        String username = principal.getName();
        CheckoutResponseDto response = checkoutService.checkoutAllItems(username);


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