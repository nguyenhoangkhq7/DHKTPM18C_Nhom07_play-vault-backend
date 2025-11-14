package fit.iuh.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import fit.iuh.dtos.UserProfileDto;
import fit.iuh.models.Customer;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    // map customer -> dto
    @Mapping(source = "account.username", target = "username")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "account.phone", target = "phone")
    UserProfileDto toDto(Customer customer);
}
