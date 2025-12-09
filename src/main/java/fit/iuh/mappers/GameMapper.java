package fit.iuh.mappers;

import fit.iuh.dtos.*;
import fit.iuh.models.*;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

    // ============================
    // BASIC INFO
    // ============================
    @Named("toBasicInfoDto")
    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.shortDescription", target = "shortDescription")
    @Mapping(source = "gameBasicInfos.description", target = "description")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.trailerUrl", target = "trailerUrl")
    @Mapping(source = "gameBasicInfos.requiredAge", target = "requiredAge")
    @Mapping(source = "gameBasicInfos.isSupportController", target = "isSupportController")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    GameBasicInfoDto toBasicInfoDto(Game game);

    // ⭐ Thêm hàm List (bạn đang thiếu)
    List<GameBasicInfoDto> toBasicInfoDtoList(List<Game> games);

    // Cái này của bạn - giữ lại (nhưng lưu ý input khác type)
    List<GameBasicInfoDto> toDtoList(List<GameBasicInfo> games);


    // ============================
    // GAME CARD DTO
    // ============================
    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    GameCardDto toCardDto(Game game);

    // ⭐ Thêm hàm List cho CardDto
    List<GameCardDto> toCardDtoList(List<Game> games);


    // ============================
    // BASIC DTO WITH STATUS
    // ============================
    @Mapping(target = "status",
            expression = "java(game.getGameBasicInfos()!=null && " +
                    "game.getGameBasicInfos().getSubmission()!=null && " +
                    "game.getGameBasicInfos().getSubmission().getStatus()!=null ? " +
                    "game.getGameBasicInfos().getSubmission().getStatus().name() : null)")
    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    GameDto toDTO(Game game);


    // ============================
    // GAME WITH RATING
    // ============================
    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.id", target = "publisherId")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    @Mapping(source = "reviews", target = "avgRating", qualifiedByName = "calculateAvgRating")
    @Mapping(source = "reviews", target = "reviewCount", qualifiedByName = "calculateReviewCount")
    @Mapping(source = "gameBasicInfos.platforms", target = "platforms", qualifiedByName = "mapPlatforms")
    @Mapping(source = "gameBasicInfos.previewImages", target = "previewImages", qualifiedByName = "mapPreviewImages")
    @Mapping(source = ".", target = "discount", qualifiedByName = "calculateFinalDiscountAmount")
    @Mapping(source = "gameBasicInfos.systemRequirement", target = "systemRequirement")
    GameWithRatingDto toGameWithRatingDto(Game game);

    List<GameWithRatingDto> toGameWithRatingDtoList(List<Game> games);


    // ============================
    // NAMED METHODS
    // ============================
    @Named("calculateAvgRating")
    default Double calculateAvgRating(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        double avg = reviews.stream()
                .mapToDouble(r -> r.getRating() != null ? r.getRating() : 0)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }

    @Named("calculateReviewCount")
    default Integer calculateReviewCount(List<Review> reviews) {
        return reviews != null ? reviews.size() : 0;
    }

    @Named("mapPlatforms")
    default List<String> mapPlatforms(List<Platform> platforms) {
        if (platforms == null) return List.of();
        return platforms.stream()
                .map(Platform::getName)
                .toList();
    }

    @Named("mapPreviewImages")
    default List<String> mapPreviewImages(List<PreviewImage> previewImages) {
        if (previewImages == null) return List.of();
        return previewImages.stream()
                .map(PreviewImage::getUrl)
                .toList();
    }

    @Named("calculateFinalDiscountAmount")
    default Double calculateFinalDiscountAmount(Game game) {
        if (game == null || game.getPromotion() == null ||
                game.getGameBasicInfos() == null || game.getGameBasicInfos().getPrice() == null) {
            return 0.0;
        }
        BigDecimal basePrice = game.getGameBasicInfos().getPrice();
        BigDecimal discountAmount = game.getPromotion().calculateDiscount(basePrice);
        return discountAmount.doubleValue();
    }

    List<GameDto> toGameDto(List<Game> games);
}
