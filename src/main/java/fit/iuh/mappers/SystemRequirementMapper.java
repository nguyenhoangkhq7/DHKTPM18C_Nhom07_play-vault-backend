package fit.iuh.mappers;

import fit.iuh.dtos.SystemRequirementDTO;
import fit.iuh.models.SystemRequirement;

public interface SystemRequirementMapper {
    SystemRequirementDTO toDto(SystemRequirement entity);
}
