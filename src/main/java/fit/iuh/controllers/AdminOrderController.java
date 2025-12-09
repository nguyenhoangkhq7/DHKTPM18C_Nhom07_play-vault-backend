package fit.iuh.controllers;

import fit.iuh.dtos.OrderTableDto;
import fit.iuh.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * API Lấy danh sách đơn hàng cho bảng Admin
     * GET /api/admin/orders?page=0&size=10&keyword=Nguyen&status=COMPLETED
     */
    @GetMapping
    public ResponseEntity<Page<OrderTableDto>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "ALL") String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderTableDto> result = orderService.getOrdersForAdmin(keyword, status, pageable);
        return ResponseEntity.ok(result);
    }
}
