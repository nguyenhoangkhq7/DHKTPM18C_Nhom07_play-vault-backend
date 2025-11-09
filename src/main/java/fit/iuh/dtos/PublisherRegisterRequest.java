package fit.iuh.dtos;

import fit.iuh.models.enums.PaymentMethod;
import fit.iuh.validation.ValidPublisherInfo;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ValidPublisherInfo
public class PublisherRegisterRequest extends AccountRegisterRequest {

   @NotBlank(message = "Tên studio không được để trống")
   private String studioName;

   private String description;
   private String website;

   // payment info
   private PaymentMethod paymentMethod;
   private String accountName;
   private String accountNumber;
   private String bankName;
}
