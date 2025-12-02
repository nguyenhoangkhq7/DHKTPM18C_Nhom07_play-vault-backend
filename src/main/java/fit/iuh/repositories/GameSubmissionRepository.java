package fit.iuh.repositories;

import fit.iuh.models.GameSubmission;
import fit.iuh.models.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameSubmissionRepository extends JpaRepository<GameSubmission, Long>, JpaSpecificationExecutor<GameSubmission> {
    // Chúng ta sẽ dùng hàm findById(ID) có sẵn của JpaRepository
    Optional<GameSubmission> findFirstByGameBasicInfos_IdOrderBySubmittedAtDesc(Long gameBasicInfoId);

    @Query("SELECT s FROM GameSubmission s " +
            "WHERE (:status IS NULL OR s.status = :status) " +
            "AND (:keyword IS NULL OR LOWER(s.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.gameBasicInfos.publisher.studioName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY s.submittedAt DESC")
    Page<GameSubmission> searchSubmissions(@Param("keyword") String keyword,
                                           @Param("status") SubmissionStatus status,
                                           Pageable pageable);

}