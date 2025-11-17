package fit.iuh.repositories;

import fit.iuh.models.CartItem;
import fit.iuh.models.GameBasicInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface GameBasicInfoRepository extends JpaRepository<GameBasicInfo, Long> {
    @Query("Select gb from Customer c join c.account join c.wishlist g join g.gameBasicInfos gb where c.account.username = :username")
    List<GameBasicInfo> findAllByGameFavoriteWithCustomerId(@Param("username") String username);

    @Query("Select gb from Customer c join c.cart join CartItem ci join Game g join GameBasicInfo gb where c.id = :customerId")
    List<GameBasicInfo> findAllGameCartByCustomerId(Long cartId);


}
