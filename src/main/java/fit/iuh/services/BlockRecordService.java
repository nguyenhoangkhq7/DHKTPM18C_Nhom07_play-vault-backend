package fit.iuh.services;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.dtos.BlockRequest;
import fit.iuh.dtos.BlockResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BlockRecordService {
    List<BlockRecordDto> getBlockRecordsByUserName(String userName);

    @Transactional
    BlockResponse block(String username, BlockRequest request);

    @Transactional
    BlockResponse unblock(String username);
}
