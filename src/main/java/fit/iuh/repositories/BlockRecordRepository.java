package fit.iuh.repositories;

import fit.iuh.models.BlockRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRecordRepository extends JpaRepository<BlockRecord, Long> {
    List<BlockRecord> findBlockRecordsByAccount_Username(String username);

    boolean existsByAccountUsernameAndIsBlockTrue(String username);

    Optional<BlockRecord> findFirstByAccountUsernameAndIsBlockTrueOrderByCreatedAtDesc(String username);

    List<BlockRecord> findByAccountUsernameOrderByCreatedAtDesc(String username);

//    Page<BlockRecord> findByAccountUsernameContainingIgnoreCaseOrReasonContainingIgnoreCase(
//            String username, String reason, Pageable pageable);
}
