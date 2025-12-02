package fit.iuh.mappers;


import fit.iuh.dtos.ReviewDto;
import fit.iuh.models.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "customer.fullName", target = "customerName")
    ReviewDto toDto(Review review);

    List<ReviewDto> toDtoList(List<Review> reviews);

}
