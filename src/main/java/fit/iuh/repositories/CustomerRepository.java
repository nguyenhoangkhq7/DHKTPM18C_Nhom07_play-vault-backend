package fit.iuh.repositories;

import fit.iuh.models.Account;
import fit.iuh.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccount(Account account);
    Optional<Customer> findByAccountUsername(String username);
}