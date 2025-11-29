package fit.iuh.repositories;

import fit.iuh.dtos.GameWithRatingDto;
import fit.iuh.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long>, JpaSpecificationExecutor<Game> {
    List<Game> findByGameBasicInfos_Category_Name(String categoryName);

//    @Query(value = "SELECT g.* " +
//            "FROM games g " +
//            "JOIN reviews r ON g.game_basic_info_id = r.game_id " +
//            "GROUP BY  g.game_basic_info_id" +
//            "HAVING AVG(r.rating) > 0 " +
//            "ORDER BY AVG(r.rating) DESC " +
//            "LIMIT :topN", nativeQuery = true)
@Query(value =
        "SELECT g.*, AVG(r.rating) AS avg_rating " +
                "FROM games g " +
                "JOIN reviews r ON g.game_basic_info_id = r.game_id " +
                "GROUP BY g.id " +
                "HAVING AVG(r.rating) > 0 " +
                "ORDER BY avg_rating DESC " +
                "LIMIT :topN",
        nativeQuery = true)
    List<Game> findTopRatedGames(@Param("topN") int topN);
    List<Game> findByStatus(String status);






}