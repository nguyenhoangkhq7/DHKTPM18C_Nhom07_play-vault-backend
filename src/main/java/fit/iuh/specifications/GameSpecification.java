// trong package fit.iuh.specifications
package fit.iuh.specifications;

import fit.iuh.models.*;
import fit.iuh.models.enums.SubmissionStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

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

    public static Specification<Game> filterApprovedGames(String searchQuery, String categoryFilter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<GameSubmission> subRoot = subquery.from(GameSubmission.class);

            subquery.select(subRoot.get("id")); // id của GameSubmission maps với gameBasicInfoId
            subquery.where(cb.equal(subRoot.get("status"), SubmissionStatus.APPROVED));

            // Điều kiện: Game.gameBasicInfos.id PHẢI NẰM TRONG danh sách ID của subquery trên
            predicates.add(root.get("gameBasicInfos").get("id").in(subquery));
            // 1. Join bảng: Game -> GameBasicInfos
            Join<Game, GameBasicInfo> basicInfoJoin = root.join("gameBasicInfos", JoinType.LEFT);

            // 2. Tìm kiếm (Search Query)
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                String likePattern = "%" + searchQuery.toLowerCase().trim() + "%";

                // a. Tìm theo tên Game (Field 'name' trong GameBasicInfo là đúng)
                Predicate nameLike = cb.like(cb.lower(basicInfoJoin.get("name")), likePattern);

                // b. Tìm theo tên Publisher (CẦN SỬA TẠI ĐÂY)
                // Join vào bảng Publisher
                Join<GameBasicInfo, Publisher> publisherJoin = basicInfoJoin.join("publisher", JoinType.LEFT);

                // LỖI TRƯỚC ĐÓ: publisherJoin.get("name") -> Sai vì Entity Publisher dùng 'studioName'
                // SỬA LẠI: Dùng "studioName" khớp với file Entity bạn cung cấp
                Predicate publisherLike = cb.like(cb.lower(publisherJoin.get("studioName")), likePattern);

                predicates.add(cb.or(nameLike, publisherLike));
            }

            // 3. Lọc theo Category
            if (categoryFilter != null && !categoryFilter.isEmpty() && !"all".equalsIgnoreCase(categoryFilter)) {
                Join<GameBasicInfo, Category> categoryJoin = basicInfoJoin.join("category", JoinType.INNER);
                predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), categoryFilter.toLowerCase()));
            }

            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}