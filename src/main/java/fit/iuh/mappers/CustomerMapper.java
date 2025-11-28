package fit.iuh.mappers;

import fit.iuh.dtos.CustomerDto;
import fit.iuh.models.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")

    // 2. Ánh xạ từ Account entity (đã sửa)
    @Mapping(source = "account.email", target = "email") // Lấy từ Account.email
    @Mapping(source = "account.createdAt", target = "date") // Lấy từ Account.createdAt
    @Mapping(source = "account.status", target = "status") // Lấy từ Account.status (MapStruct xử lý enum -> String)

    @Mapping(source = "account.username", target = "username")
    CustomerDto toCustomerDto(Customer customer);


    List<CustomerDto> toCustomerDtoList(List<Customer> customerList);
}
