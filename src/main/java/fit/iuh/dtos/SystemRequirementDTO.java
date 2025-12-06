package fit.iuh.dtos;

import fit.iuh.models.enums.Os;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemRequirementDTO {
    private Os os;
    private String cpu;
    private String gpu;
    private String storage;
    private String ram;
}
