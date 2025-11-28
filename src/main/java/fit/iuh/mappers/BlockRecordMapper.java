package fit.iuh.mappers;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.dtos.BlockResponse;
import fit.iuh.models.BlockRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlockRecordMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "reason", target = "reason")
    BlockRecordDto toDto(BlockRecord blockRecord);

    List<BlockRecordDto> toDto(List<BlockRecord> blockRecords);


    //    BlockRecordMapper INSTANCE = Mappers.getMapper(BlockRecordMapper.class);
    @Mapping(target = "blockId", source = "id")
    @Mapping(target = "username", source = "account.username")
    @Mapping(target = "blockedAt", source = "createdAt")           // QUAN TRỌNG: map createdAt → blockedAt
//    @Mapping(target = "unblockedAt", source = "unblockedAt")
    @Mapping(target = "isCurrentlyBlocked", source = "isBlock")
    @Mapping(target = "action", ignore = true)      // sẽ set thủ công: BLOCKED / UNBLOCKED
    @Mapping(target = "message", ignore = true)
    // sẽ set thủ công
    BlockResponse toResponse(BlockRecord entity);
}
