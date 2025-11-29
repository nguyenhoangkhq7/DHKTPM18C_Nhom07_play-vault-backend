// src/main/java/fit/iuh/dtos/GameSimpleDto.java
package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSimpleDto {
    private Long id;
    private String name;
    private String thumbnail;
    private BigDecimal price;
    private Double rating;
    private int reviewCount;
}