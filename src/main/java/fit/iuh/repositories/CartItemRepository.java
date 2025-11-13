package fit.iuh.repositories;

import fit.iuh.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("Select ci from Customer c join c.cart join CartItem ci where c.id = :customerId")
    List<CartItem> findAllGameItemByCustomerId(Long cartId);
}
