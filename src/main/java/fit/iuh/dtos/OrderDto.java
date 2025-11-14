package fit.iuh.dtos;

import fit.iuh.models.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderDto {
    private Long id;
    private String orderCode; // nếu bạn có trường này, hoặc dùng id
    private LocalDate createdAt;
    private BigDecimal total;
    private OrderStatus status;
    private List<OrderItemDto> items;
}
