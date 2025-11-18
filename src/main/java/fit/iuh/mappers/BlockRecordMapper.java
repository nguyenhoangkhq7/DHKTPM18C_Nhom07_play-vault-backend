package fit.iuh.mappers;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.models.BlockRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlockRecordMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "reason", target = "reason")
    BlockRecordDto toDto(BlockRecord blockRecord);
    List<BlockRecordDto> toDto(List<BlockRecord> blockRecords);
}
