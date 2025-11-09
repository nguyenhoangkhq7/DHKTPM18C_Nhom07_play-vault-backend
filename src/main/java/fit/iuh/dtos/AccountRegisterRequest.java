package fit.iuh.dtos;

import fit.iuh.models.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccountRegisterRequest {
   @NotBlank(message = "Username không được để trống")
   @Size(min = 4, max = 20, message = "Username phải từ 4 đến 20 ký tự")
   private String username;

   @NotBlank(message = "Password không được để trống")
   @Size(min = 8, message = "Password phải ít nhất 8 ký tự")
   private String password;

   @NotBlank(message = "Email không được để trống")
   @Email(message = "Email không hợp lệ")
   private String email;

   @NotBlank(message = "Số điện thoại không được để trống")
   @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
   private String phone;

   @NotNull(message = "Role không được để trống")
   private Role role;
}

