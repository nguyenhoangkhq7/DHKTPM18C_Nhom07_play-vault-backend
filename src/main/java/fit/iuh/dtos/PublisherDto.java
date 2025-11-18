package fit.iuh.dtos;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherDto {
    private Long id;
    private String name; // Tên Studio, lấy từ studioName
    private String email; // Lấy từ Account entity
    private LocalDate date; // Lấy từ Account entity
    private Integer games; // Số game
    private String status;
    private String username;
}
