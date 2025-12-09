package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderTableDto {
    private Long id;                // Dùng làm Mã ĐH
    private String orderCode;       // Hiển thị dạng "ORD-001"
    private String customerName;    // Người mua
    private String email;           // Email
    private int gameCount;          // SL Game
    private BigDecimal total;       // Tổng tiền
    private LocalDate createdAt;    // Ngày tạo
    private String status;          // Trạng thái (PENDING, COMPLETED...)
}