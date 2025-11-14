package fit.iuh.mappers;

import fit.iuh.dtos.CartItemDto;
import fit.iuh.dtos.GameBasicInfoDto;
import fit.iuh.dtos.GameCardDto;
import fit.iuh.models.CartItem;
import fit.iuh.models.Game;
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
    GameBasicInfoDto toBasicInfoDto(Game game);

    List<GameBasicInfoDto> toDtoList(List<Game> games);

    @Mapping(source = "gameBasicInfos.name", target = "name")
    @Mapping(source = "gameBasicInfos.thumbnail", target = "thumbnail")
    @Mapping(source = "gameBasicInfos.price", target = "price")
    GameCardDto toCardDto(Game game);

    @Mapping(target = "game", source = "game", qualifiedByName = "toBasicInfoDto")
    @Mapping(target = "finalPrice", expression = "java(cartItem.getPrice().subtract(cartItem.getDiscount() != null ? cartItem.getDiscount() : java.math.BigDecimal.ZERO))")
    CartItemDto toCartItemDto(CartItem cartItem);

    List<CartItemDto> toCartItemDtoList(List<CartItem> cartItems);
}