package fit.iuh.repositories;

import fit.iuh.models.BlockRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRecordRepository extends JpaRepository<BlockRecord, Long> {
    List<BlockRecord> findBlockRecordsByAccount_Username(String username);
}
