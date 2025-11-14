// trong package fit.iuh.specifications
package fit.iuh.specifications;

import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal; // Sử dụng BigDecimal cho giá
import java.util.ArrayList;
import java.util.List;

public class GameSpecification {

    public static Specification<Game> filterBy(
            String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // *** Bước quan trọng: JOIN từ Game (root) sang GameBasicInfo ***
            Join<Game, GameBasicInfo> basicInfoJoin = root.join("gameBasicInfos");

            // 1. Lọc theo keyword (Search)
            // Tìm trong 'name' của GameBasicInfo
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKeyword = "%" + keyword.toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(basicInfoJoin.get("name")), searchKeyword);
                // Bạn cũng có thể tìm trong description nếu muốn:
                // Predicate descLike = cb.like(cb.lower(basicInfoJoin.get("description")), searchKeyword);
                // predicates.add(cb.or(nameLike, descLike));
                predicates.add(nameLike);
            }

            // 2. Lọc theo Thể loại (Category)
            // Lấy 'category' từ 'basicInfoJoin'
            if (categoryId != null) {
                // Tương đương JOIN GameBasicInfo -> Category
                predicates.add(cb.equal(basicInfoJoin.get("category").get("id"), categoryId));
            }

            // 3. Lọc theo Giá (Price)
            // Lấy 'price' từ 'basicInfoJoin'
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(basicInfoJoin.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(basicInfoJoin.get("price"), maxPrice));
            }

            // Cần distinct để tránh trùng lặp kết quả khi join
            query.distinct(true);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}