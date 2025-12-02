// fit.iuh.repositories.ReportRepository.java
package fit.iuh.repositories;

import fit.iuh.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByCustomer_Account_UsernameOrderByCreatedAtDesc(String username);
    List<Report> findAllByOrderByCreatedAtDesc();
}