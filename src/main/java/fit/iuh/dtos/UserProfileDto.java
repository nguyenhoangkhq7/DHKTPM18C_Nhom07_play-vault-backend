package fit.iuh.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserProfileDto {
    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private BigDecimal balance;

    private String username;  // lấy từ Account
    private String email;     // lấy từ Account
    private String phone;     // lấy từ Account

    private String address;   // vẫn lấy từ customers nếu bạn giữ cột này
    private String avatarUrl; // nếu bạn muốn hiển thị avatar    // optional
}

