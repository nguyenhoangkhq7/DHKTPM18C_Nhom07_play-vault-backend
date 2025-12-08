package fit.iuh.dtos;

import fit.iuh.models.enums.Os;
import lombok.Data;

@Data
public class UserSystemSpecDTO {
    private Os os;
    private String cpu;
    private String gpu;
    private String ram;
    private String storage;
}
