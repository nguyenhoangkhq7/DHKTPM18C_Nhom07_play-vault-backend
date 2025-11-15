package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCardDto {
    private Long id;
    private String name;
    private String thumbnail; // Đường dẫn tới ảnh bìa
    private BigDecimal price; // Giá của game (từ GameBasicInfo)
}


