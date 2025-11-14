package fit.iuh.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UpdateAccountRequest {
    @Email
    private String email;

    @Size(max = 20)
    private String phone;
}
