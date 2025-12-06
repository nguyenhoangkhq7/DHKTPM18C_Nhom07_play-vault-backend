package fit.iuh.dtos;

import fit.iuh.models.SystemRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemRequirementRepository extends JpaRepository<SystemRequirement, Long> {
    // Nếu cần custom truy vấn, có thể viết thêm ở đây sau
}
