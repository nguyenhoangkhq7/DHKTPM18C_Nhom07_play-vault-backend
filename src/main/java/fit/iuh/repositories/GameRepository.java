package fit.iuh.repositories;

import fit.iuh.models.Cart;
import fit.iuh.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
