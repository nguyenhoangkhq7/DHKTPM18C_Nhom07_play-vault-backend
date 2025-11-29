package fit.iuh.specifications;

import fit.iuh.models.Category;
import fit.iuh.models.Game;
import fit.iuh.models.GameSubmission;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.enums.SubmissionStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate; // Dùng Jakarta Persistence Predicate
import java.util.ArrayList;
import java.util.List;

public class GameSubmissionSpecification {

    public static Specification<GameSubmission> filterPendingSubmissions(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            // Sử dụng đúng loại Predicate của JPA Criteria API
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("status"), SubmissionStatus.PENDING));

            if (StringUtils.hasText(searchQuery)) {
                String likeKeyword = "%" + searchQuery.toLowerCase() + "%";

                Join<GameSubmission, GameBasicInfo> basicInfoJoin = root.join("gameBasicInfos");

                Predicate nameLike = criteriaBuilder.like(criteriaBuilder.lower(basicInfoJoin.get("name")), likeKeyword);
                Predicate publisherLike = criteriaBuilder.like(criteriaBuilder.lower(basicInfoJoin.get("publisher").get("name")), likeKeyword);

                predicates.add(criteriaBuilder.or(nameLike, publisherLike));
            }

            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


}