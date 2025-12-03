package fit.iuh.dtos;

import lombok.Data;

@Data
public class GameUpdateRequest {
    private String status; // "pending", "approved", "rejected"
}
