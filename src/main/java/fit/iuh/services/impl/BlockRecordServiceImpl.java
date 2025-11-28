package fit.iuh.services.impl;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.dtos.BlockRequest;
import fit.iuh.dtos.BlockResponse;
import fit.iuh.mappers.BlockRecordMapper;
import fit.iuh.models.Account;
import fit.iuh.models.BlockRecord;
import fit.iuh.models.enums.AccountStatus;
import fit.iuh.repositories.AccountRepository;
import fit.iuh.repositories.BlockRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BlockRecordServiceImpl implements fit.iuh.services.BlockRecordService {
    private final BlockRecordRepository blockRecordRepository;
    private final BlockRecordMapper blockRecordMapper;
    private final AccountRepository accountRepository;

    @Override
    public List<BlockRecordDto> getBlockRecordsByUserName(String userName) {
        return blockRecordMapper.toDto(blockRecordRepository.findBlockRecordsByAccount_Username(userName));
    }

    @Transactional
    @Override
    public BlockResponse block(String username, BlockRequest request) {

        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản: " + username));

        if (blockRecordRepository.existsByAccountUsernameAndIsBlockTrue(username)) {
            throw new IllegalStateException("Tài khoản đã bị khóa rồi!");
        }

        BlockRecord entity = new BlockRecord(
                account,
                request.reason(),
                true,
                LocalDate.now());

        blockRecordRepository.save(entity);
        account.setStatus(AccountStatus.BANNED);
        accountRepository.save(account);

        BlockResponse response = blockRecordMapper.toResponse(entity);
        return new BlockResponse(
                response.blockId(),
                response.username(),
                response.reason(),
                response.blockedAt(),
//                response.unblockedAt(),
                true,
                "BLOCKED",
                "Đã khóa tài khoản thành công"
        );
    }

    @Transactional
    @Override
    public BlockResponse unblock(String username) {

        BlockRecord entity = blockRecordRepository
                .findFirstByAccountUsernameAndIsBlockTrueOrderByCreatedAtDesc(username)
                .orElseThrow(() -> new IllegalStateException("Tài khoản chưa bị khóa!"));

        entity.setIsBlock(false);
        entity.setCreatedAt(LocalDate.now());
        blockRecordRepository.save(entity);

        Account account = entity.getAccount();
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);

        BlockResponse response = blockRecordMapper.toResponse(entity);
        return new BlockResponse(
                response.blockId(),
                response.username(),
                response.reason(),
                response.blockedAt(),
//                LocalDateTime.now(),
                false,
                "UNBLOCKED",
                "Đã gỡ khóa tài khoản thành công"
        );
    }

}