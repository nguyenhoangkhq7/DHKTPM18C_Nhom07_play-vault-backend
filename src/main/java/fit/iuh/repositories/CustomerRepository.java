package fit.iuh.repositories;

import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByAccount(Account account);

    Optional<Customer> findByAccountUsername(String username);

    @Query("""
            SELECT c FROM Customer c
            LEFT JOIN FETCH c.library g
            LEFT JOIN FETCH g.gameBasicInfos
            WHERE c.account.username = :username
            """)
    Optional<Customer> findByAccount_UsernameWithLibrary(@Param("username") String username);

}