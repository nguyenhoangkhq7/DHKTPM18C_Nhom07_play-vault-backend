package fit.iuh.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemRequirementDTO {
    private Long id;

    private String os;
    private String cpu;
    private String gpu;
    private String storage;
    private String ram;
}
