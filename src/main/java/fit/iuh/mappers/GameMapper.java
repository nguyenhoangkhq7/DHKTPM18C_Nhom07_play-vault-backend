package fit.iuh.mappers;

import fit.iuh.dtos.*;
import fit.iuh.models.*;
import org.mapstruct.*;


import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GameMapper {

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
    GameBasicInfoDto toBasicInfoDto(Game game); // Input là Game

    List<GameBasicInfoDto> toDtoList(List<Game> games);

    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    GameCardDto toCardDto(Game game);

    @Mapping(target = "game", source = "game", qualifiedByName = "toBasicInfoDto")
    @Mapping(target = "finalPrice", expression = "java(cartItem.getPrice().subtract(cartItem.getDiscount() != null ? cartItem.getDiscount() : java.math.BigDecimal.ZERO))")
    CartItemDto toCartItemDto(CartItem cartItem);

    List<CartItemDto> toCartItemDtoList(List<CartItem> cartItems);


    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    GameDto toDTO(Game game);


    @Mapping(source = "gameBasicInfos", target = "gameBasicInfos")
    @Mapping(source = "gameBasicInfos.category.name", target = "categoryName")
    @Mapping(source = "gameBasicInfos.publisher.id", target = "publisherId")
    @Mapping(source = "gameBasicInfos.publisher.studioName", target = "publisherName")
    @Mapping(source = "reviews", target = "avgRating", qualifiedByName = "calculateAvgRating")
    @Mapping(source = "reviews", target = "reviewCount", qualifiedByName = "calculateReviewCount")
    @Mapping(source = "gameBasicInfos.platforms", target = "platforms", qualifiedByName = "mapPlatforms")
    @Mapping(source = "gameBasicInfos.previewImages", target = "previewImages", qualifiedByName = "mapPreviewImages")
    @Mapping(source = "game", target = "discount", qualifiedByName = "calculateFinalDiscountAmount")
    @Mapping(source = "gameBasicInfos.systemRequirement", target = "systemRequirement") // thêm dòng này
    GameWithRatingDto toGameWithRatingDto(Game game);

    List<GameWithRatingDto> toGameWithRatingDtoList(List<Game> games);

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

    // Map object Platform -> list name
    @Named("mapPlatforms")
    default List<String> mapPlatforms(List<Platform> platforms) {
        if (platforms == null) return List.of();
        return platforms.stream()
                .map(Platform::getName)
                .toList();
    }

    // Map object Platform -> list name
    @Named("mapPreviewImages")
    default List<String> mapPreviewImages(List<PreviewImage> mapPreviewImages) {
        if (mapPreviewImages == null) return List.of();
        return mapPreviewImages.stream()
                .map(PreviewImage::getUrl)
                .toList();
    }

    /**
     * Tính số tiền giảm giá cuối cùng (BigDecimal) và chuyển sang Double
     * Dựa trên Game và giá gốc của nó.
     * @param game Đối tượng Game đầy đủ
     * @return Số tiền giảm giá thực tế (Double)
     */
    @Named("calculateFinalDiscountAmount")
    default Double calculateFinalDiscountAmount(Game game) {
        if (game.getPromotion() == null || game.getGameBasicInfos() == null || game.getGameBasicInfos().getPrice() == null) {
            return 0.0;
        }

        BigDecimal basePrice = game.getGameBasicInfos().getPrice();

        // 🎯 GỌI HÀM BUSINESS LOGIC TỪ ĐỐI TƯỢNG PROMOTION
        BigDecimal discountAmount = game.getPromotion().calculateDiscount(basePrice);

        // Trả về số tiền giảm giá dưới dạng Double (cần thiết cho DTO)
        return discountAmount.doubleValue();
    }
}