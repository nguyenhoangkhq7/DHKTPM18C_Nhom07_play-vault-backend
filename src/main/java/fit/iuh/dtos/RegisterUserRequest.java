package fit.iuh.dtos;

import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.validation.ValidPublisherInfo;
import fit.iuh.validation.ValidRole;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@ValidPublisherInfo
public class RegisterUserRequest {

   // common account fields
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

   @ValidRole
   @NotBlank(message = "Role không được để trống")
   private String role;

   // customer fields
   @NotBlank(message = "Họ tên không được để trống")
   @Size(max = 50, message = "Họ tên tối đa 50 ký tự")
   private String fullName;

   @NotNull(message = "Ngày sinh không được để trống")
   @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
   private LocalDate dateOfBirth;

   // publisher fields
   private String studioName;
   private String description;
   private String website;

   // payment info
   private PaymentMethod paymentMethod;
   private String accountName;
   private String accountNumber;
   private String bankName;
}
