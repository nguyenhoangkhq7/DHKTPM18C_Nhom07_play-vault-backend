package fit.iuh.controllers;

import fit.iuh.dtos.OrderHistoryResponse;
import fit.iuh.services.JwtService;
import fit.iuh.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

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
}