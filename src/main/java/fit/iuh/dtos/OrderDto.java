package fit.iuh.dtos;

import fit.iuh.models.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderDTO {

    private Long id;
    private String orderCode; // nếu bạn có trường này, hoặc dùng id

    @NotNull(message = "Ngày tạo đơn không được để trống")
    @PastOrPresent(message = "Ngày tạo đơn không thể ở tương lai")
    private LocalDate createdAt;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng tiền phải >= 0")
    private BigDecimal total;

    @NotNull(message = "Trạng thái đơn hàng không được để trống")
    private OrderStatus status;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Tổng tiền phải >= 0")
    private BigDecimal total;

    @NotNull(message = "ID khách hàng không được để trống")
    private Long customerId;

    private Long paymentId;

    @Valid
    @NotEmpty(message = "Danh sách order items không được để trống")
    private List<OrderItemDTO> orderItems;
}
