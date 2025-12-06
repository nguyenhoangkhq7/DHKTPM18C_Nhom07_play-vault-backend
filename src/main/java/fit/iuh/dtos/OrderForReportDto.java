// fit.iuh.dtos.OrderForReportDto.java
package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OrderForReportDto {
    private Long id;
    private String orderCode;      // ORD-003
    private BigDecimal total;
    private LocalDate createdAt;
}