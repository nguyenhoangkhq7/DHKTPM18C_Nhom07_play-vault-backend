package fit.iuh.services;

import fit.iuh.dtos.AccountDto;
import fit.iuh.dtos.OrderItemDto;
import fit.iuh.mappers.AccountMapper;
import fit.iuh.mappers.OrderItemMapper;
import fit.iuh.models.Account;
import fit.iuh.models.OrderItem;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService{
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAccountActiveToday() {
        List<Account> items = accountRepository.findAllAccountCreateToday();
        return accountMapper.toDTO(items);
    }


}
