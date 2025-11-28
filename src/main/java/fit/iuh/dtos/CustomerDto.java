package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    private String fullName; //
    private String email; // Lấy từ Account entity
    private LocalDate date; // Lấy từ Account entity
    private String status;
    private String username; //Lấy từ Account entity
}
