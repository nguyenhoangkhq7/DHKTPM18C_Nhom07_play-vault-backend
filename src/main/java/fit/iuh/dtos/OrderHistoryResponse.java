package fit.iuh.dtos;

import fit.iuh.models.enums.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrderHistoryResponse {
    @NotNull(message = "Order ID cannot be null")
    private Long id;

    @NotBlank(message = "Order code cannot be empty")
    private String orderCode;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Order date cannot be in the future")
    private LocalDate date;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus status;

    @NotNull(message = "Total price cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Total price must be >= 0")
    private BigDecimal totalPrice;

    @NotNull(message = "Purchased games list cannot be null")
    private List<PurchasedGameResponse> games;
}