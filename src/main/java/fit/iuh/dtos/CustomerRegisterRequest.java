package fit.iuh.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerRegisterRequest extends AccountRegisterRequest {

   @NotBlank(message = "Họ tên không được để trống")
   @Size(max = 50, message = "Họ tên tối đa 50 ký tự")
   private String fullName;

   @NotNull(message = "Ngày sinh không được để trống")
   @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại")
   private LocalDate dateOfBirth;
}
