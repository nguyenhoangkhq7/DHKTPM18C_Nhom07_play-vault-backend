package fit.iuh.repositories;

import fit.iuh.models.Account;
import fit.iuh.models.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

   // Tìm Publisher theo Account object
   Optional<Publisher> findByAccount(Account account);

   // Tìm Publisher theo username của Account
   Optional<Publisher> findByAccount_Username(String username);

   // Tìm Publisher theo username và fetch các liên quan nếu cần (ví dụ PaymentInfo)
   @Query("""
            SELECT p FROM Publisher p
            LEFT JOIN FETCH p.paymentInfo
            WHERE p.account.username = :username
            """)
   Optional<Publisher> findByAccount_UsernameWithPaymentInfo(@Param("username") String username);
}
