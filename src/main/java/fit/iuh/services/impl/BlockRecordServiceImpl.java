package fit.iuh.services.impl;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.mappers.BlockRecordMapper;
import fit.iuh.models.BlockRecord;
import fit.iuh.repositories.BlockRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BlockRecordServiceImpl implements fit.iuh.services.BlockRecordService {
    private final BlockRecordRepository blockRecordRepository;
    private final BlockRecordMapper blockRecordMapper;

    @Override
    public List<BlockRecordDto> getBlockRecordsByUserName(String userName){
        return blockRecordMapper.toDto(blockRecordRepository.findBlockRecordsByAccount_Username(userName));
    }
}
