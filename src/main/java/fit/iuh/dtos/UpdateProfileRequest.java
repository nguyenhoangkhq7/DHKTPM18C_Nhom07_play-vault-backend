package fit.iuh.dtos;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class UpdateProfileRequest {
    @Size(max = 200)
    private String fullName;

    // yyyy-MM-dd hoặc bạn có thể dùng LocalDate với @JsonFormat nếu muốn
    private String dateOfBirth;

    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String avatarUrl;
}
