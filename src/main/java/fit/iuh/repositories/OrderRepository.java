package fit.iuh.repositories;

import fit.iuh.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Nếu Order.customer là đối tượng Customer và trường id là customer.id
    Page<Order> findByCustomer_Id(Long customerId, Pageable pageable);
}
