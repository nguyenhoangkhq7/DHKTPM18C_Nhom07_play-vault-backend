package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Integer rating;
    private String comment;
    private LocalDate createdAt;
    private String customerName;
}
