package fit.iuh.repositories;

import fit.iuh.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    // Lọc game theo categoryName
    List<Game> findByGameBasicInfos_Category_Name(String categoryName);

    //Lấy top n game được đánh giá cao nhất
    @Query(value = "SELECT g.* " +
            "FROM games g " +
            "JOIN reviews r ON g.game_basic_info_id = r.game_id " +
            "GROUP BY g.game_basic_info_id " +
            "ORDER BY AVG(r.rating) DESC " +
            "LIMIT :topN", nativeQuery = true)
    List<Game> findTopRatedGames(@Param("topN") int topN);
}
