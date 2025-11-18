package fit.iuh.repositories;

import fit.iuh.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM Order o JOIN o.orderItems oi WHERE o.createdAt = current_date() AND o.status = 'COMPLETED'")  // Tinh chỉnh: uppercase SELECT, thêm () cho current_date
    List<OrderItem> findAllByOrderItemToday();
}