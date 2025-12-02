package fit.iuh.mappers;


import fit.iuh.dtos.AccountDto;
import fit.iuh.models.Account;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface AccountMapper {
    List<AccountDto> toDTO(List<Account> accounts);

}
