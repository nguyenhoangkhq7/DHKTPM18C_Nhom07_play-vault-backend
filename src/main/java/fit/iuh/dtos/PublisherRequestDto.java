package fit.iuh.dtos;

import fit.iuh.models.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublisherRequestDto {
    private String id;
    private RequestStatus status;
}
