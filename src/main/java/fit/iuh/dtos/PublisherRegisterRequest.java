package fit.iuh.dtos;

import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.validation.ValidPublisherInfo;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@ValidPublisherInfo
public class PublisherRegisterRequest {

   // --- 1. Thông tin Account ---
   @NotBlank(message = "Username không được để trống")
   private String userName;
   @NotBlank(message = "Password không được để trống")
   private String password;
   @Email(message = "Email không hợp lệ")
   @NotBlank
   private String email;
   private String phone;

   @NotBlank(message = "Tên studio không được để trống")
   private String studioName;
   private String description;
   private String website;

   // payment info
   @NotNull(message = "Phương thức thanh toán không được để trống")
   private PaymentMethod paymentMethod;
   @NotBlank
   private String accountName;
   @NotBlank
   private String accountNumber;
   @NotBlank
   private String bankName;
}
