package fit.iuh.dtos;

import lombok.Data; // 1. Import dòng này
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // 2. Bắt buộc có dòng này để tự tạo getName(), setName()
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateDto {
    private String name;
    private Double price;
    private String shortDescription;
    private String description;
    private Long categoryId;
    private String trailerUrl;
    private Boolean isFree;
}