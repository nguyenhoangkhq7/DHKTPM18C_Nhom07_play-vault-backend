// MonthlyRevenueDto.java
package fit.iuh.dtos;

import java.math.BigDecimal;

public record MonthlyRevenueDto(int month, BigDecimal revenue) {}