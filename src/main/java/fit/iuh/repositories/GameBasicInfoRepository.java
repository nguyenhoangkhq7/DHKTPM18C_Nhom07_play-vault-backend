package fit.iuh.repositories;

import fit.iuh.dtos.GameInfoAI;
import fit.iuh.models.CartItem;
import fit.iuh.models.Game;
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

    @Query("""
    SELECT new fit.iuh.dtos.GameInfoAI(
        g.id,
        g.gameBasicInfos.name,
        g.gameBasicInfos.shortDescription,
        g.gameBasicInfos.description,
        g.gameBasicInfos.price,
        g.gameBasicInfos.thumbnail,
        g.gameBasicInfos.trailerUrl,
        g.gameBasicInfos.requiredAge
    )
    FROM Game g
    WHERE LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    List<GameInfoAI> findInfosByName(@Param("name") String name);

    @Query("Select gb from Customer c join c.cart join CartItem ci join Game g join GameBasicInfo gb where c.id = :customerId")
    List<GameBasicInfo> findAllGameCartByCustomerId(@Param("customerId") Long customerId);


    @Query("""
    SELECT new fit.iuh.dtos.GameInfoAI(
        g.id,
        g.gameBasicInfos.name,
        g.gameBasicInfos.shortDescription,
        g.gameBasicInfos.description,
        g.gameBasicInfos.price,
        g.gameBasicInfos.thumbnail,
        g.gameBasicInfos.trailerUrl,
        g.gameBasicInfos.requiredAge
    )
    FROM Game g
    WHERE LOWER(g.gameBasicInfos.category.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    List<GameInfoAI> findByTypeGame(@Param("name") String name);


    @Query("""
    SELECT new fit.iuh.dtos.GameInfoAI(
        g.id,
        g.gameBasicInfos.name,
        g.gameBasicInfos.shortDescription,
        g.gameBasicInfos.description,
        g.gameBasicInfos.price,
        g.gameBasicInfos.thumbnail,
        g.gameBasicInfos.trailerUrl,
        g.gameBasicInfos.requiredAge
    )
    FROM Game g
    LEFT JOIN g.gameBasicInfos.systemRequirement req
    WHERE
        (:os IS NULL OR LOWER(req.os) LIKE LOWER(CONCAT('%', :os, '%')))
    OR (:cpu IS NULL OR LOWER(req.cpu) LIKE LOWER(CONCAT('%', :cpu, '%')))
    OR (:gpu IS NULL OR LOWER(req.gpu) LIKE LOWER(CONCAT('%', :gpu, '%')))
    OR (:ram IS NULL OR req.ram <= :ram)
    OR (:storage IS NULL OR req.storage <= :storage)
""")
    List<GameInfoAI> findBySystem(
            @Param("os") String os,
            @Param("cpu") String cpu,
            @Param("gpu") String gpu,
            @Param("ram") Integer ram,
            @Param("storage") Integer storage
    );

    @Query("""
    SELECT new fit.iuh.dtos.GameInfoAI(
        g.id,
        g.gameBasicInfos.name,
        g.gameBasicInfos.shortDescription,
        g.gameBasicInfos.description,
        g.gameBasicInfos.price,
        g.gameBasicInfos.thumbnail,
        g.gameBasicInfos.trailerUrl,
        g.gameBasicInfos.requiredAge
    )
    FROM Game g
    LEFT JOIN g.gameBasicInfos.systemRequirement sys
    WHERE
        (:os IS NULL OR LOWER(sys.os) LIKE LOWER(CONCAT('%', :os, '%')))
    OR (:cpu IS NULL OR LOWER(sys.cpu) LIKE LOWER(CONCAT('%', :cpu, '%')))
    OR (:gpu IS NULL OR LOWER(sys.gpu) LIKE LOWER(CONCAT('%', :gpu, '%')))
    OR (:ram IS NULL OR sys.ram <= :ram)
    OR (:storage IS NULL OR sys.storage <= :storage)
    OR (:keyword IS NULL OR LOWER(g.gameBasicInfos.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    OR (:categoryname IS NULL OR g.gameBasicInfos.category.name = :categoryname)
    OR (
         :minRating IS NULL OR
         (SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.game.id = g.id) >= :minRating
    )
    OR (:maxPrice IS NULL OR g.gameBasicInfos.price <= :maxPrice)
""")
    List<GameInfoAI> searchAdvanced(
            @Param("os") String os,
            @Param("cpu") String cpu,
            @Param("gpu") String gpu,
            @Param("ram") Integer ram,
            @Param("storage") Integer storage,
            @Param("keyword") String keyword,
            @Param("categoryname") String categoryname,
            @Param("minRating") Double minRating,
            @Param("maxPrice") Double maxPrice
    );



    Long countGameBasicInfoByPublisher_Id(Long publisherId);

}
