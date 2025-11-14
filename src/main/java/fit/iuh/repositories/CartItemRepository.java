package fit.iuh.repositories;

import fit.iuh.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // Tự động tạo câu lệnh: SELECT * FROM cart_item WHERE cart_id = ? AND game_id = ?
    Optional<CartItem> findByCartIdAndGameId(Long cartId, Long gameId);
}