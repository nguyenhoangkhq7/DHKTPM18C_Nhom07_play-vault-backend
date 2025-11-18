package fit.iuh.dtos;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherDto {
    private Long id;
    private String ten; // Tên Studio, lấy từ studioName
    private String email; // Lấy từ Account entity
    private LocalDate ngayTao; // Lấy từ Account entity
    private Integer soGame; // Số game
    private String trangThai;
}
