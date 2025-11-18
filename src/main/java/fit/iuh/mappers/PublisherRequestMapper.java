package fit.iuh.mappers;

import fit.iuh.dtos.PublisherRequestDto;
import fit.iuh.models.PublisherRequest;
import fit.iuh.services.GameBasicInfoService;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublisherRequestMapper {
    PublisherRequestDto toPublisherRequestDto(PublisherRequest publisherRequest);
}
