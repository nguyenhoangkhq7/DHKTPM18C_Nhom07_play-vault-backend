package fit.iuh.mappers;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.models.Publisher;
import fit.iuh.services.GameBasicInfoService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = GameBasicInfoService.class)
public interface PublisherMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "studioName", target = "ten")

    // 2. Ánh xạ từ Account entity (đã sửa)
    @Mapping(source = "account.email", target = "email") // Lấy từ Account.email
    @Mapping(source = "account.createdAt", target = "ngayTao") // Lấy từ Account.createdAt
    @Mapping(source = "account.status", target = "trangThai") // Lấy từ Account.status (MapStruct xử lý enum -> String)

    // 3. Ánh xạ tùy chỉnh (dùng Service)
    @Mapping(source = "publisher", target = "soGame")
    PublisherDto toPublisherDTO(Publisher publisher);

    List<PublisherDto> toPublisherDTOs(List<Publisher> publishers);
}
