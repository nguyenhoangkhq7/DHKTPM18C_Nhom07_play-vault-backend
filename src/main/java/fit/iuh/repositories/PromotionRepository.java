package fit.iuh.repositories;

import fit.iuh.models.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByPublisher_Account_UsernameOrderByStartDateDesc(String username);

    // TÌM KIẾM + LỌC NÂNG CAO
    @Query("""
        SELECT p FROM Promotion p
        WHERE p.publisher.account.username = :username
          AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:fromDate IS NULL OR p.startDate >= :fromDate)
          AND (:toDate IS NULL OR p.endDate <= :toDate)
          AND (
                :status = 'ALL'
                OR (:status = 'ACTIVE' AND p.isActive = true AND p.startDate <= CURRENT_DATE AND p.endDate >= CURRENT_DATE)
                OR (:status = 'UPCOMING' AND p.startDate > CURRENT_DATE)
                OR (:status = 'EXPIRED' AND p.endDate < CURRENT_DATE)
              )
        """)
    Page<Promotion> searchMyPromotions(
            @Param("username") String username,
            @Param("keyword") String keyword,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("status") String status,
            Pageable pageable);
}