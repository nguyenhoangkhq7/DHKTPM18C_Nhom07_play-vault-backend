package fit.iuh.services;

import fit.iuh.dtos.BlockRecordDto;

import java.util.List;

public interface BlockRecordService {
    List<BlockRecordDto> getBlockRecordsByUserName(String userName);
}
