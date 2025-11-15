package fit.iuh.repositories;

import fit.iuh.models.GameSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSubmissionRepository extends JpaRepository<GameSubmission, Long> {
    // Chúng ta sẽ dùng hàm findById(ID) có sẵn của JpaRepository
}