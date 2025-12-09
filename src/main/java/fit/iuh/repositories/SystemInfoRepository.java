package fit.iuh.repositories;


import fit.iuh.models.SystemInfo;
import fit.iuh.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemInfoRepository extends JpaRepository<SystemInfo, Long> {
    Optional<SystemInfo> findByCustomer(Customer customer);
    boolean existsByCustomer(Customer customer);
    Optional<SystemInfo> findFirstByCustomerOrderByLastUpdatedDesc(Customer customer);
    void deleteByCustomer(Customer customer);
}