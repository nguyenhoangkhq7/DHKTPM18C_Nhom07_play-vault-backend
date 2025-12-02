package fit.iuh.controllers;

import fit.iuh.dtos.*;
import fit.iuh.models.Order;
import fit.iuh.models.enums.OrderStatus;
import fit.iuh.repositories.OrderRepository;
import fit.iuh.services.CheckoutService;
import fit.iuh.services.JwtService;
import fit.iuh.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor  // ← Quan trọng: Lombok tự inject
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OrderController {
    private final OrderRepository orderRepository;  // ← DÒNG NÀY LÀM BIẾN MẤT LỖI

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

        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-orders-for-report")
    public ResponseEntity<List<OrderForReportDto>> getMyOrdersForReport() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Order> orders = orderRepository.findByCustomer_Account_UsernameAndStatusOrderByCreatedAtDesc(
                username, OrderStatus.COMPLETED); // chỉ lấy đơn đã hoàn tất

        List<OrderForReportDto> dtos = orders.stream()
                .map(order -> new OrderForReportDto(
                        order.getId(),
                        String.format("ORD-%03d", order.getId()),
                        order.getTotal(),
                        order.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}