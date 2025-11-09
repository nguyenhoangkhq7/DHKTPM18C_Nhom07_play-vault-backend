package fit.iuh.validation;

import fit.iuh.dtos.RegisterUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PublisherInfoValidator implements ConstraintValidator<ValidPublisherInfo, RegisterUserRequest> {

   @Override
   public boolean isValid(RegisterUserRequest dto, ConstraintValidatorContext context) {
      if (dto.getRole() != null && dto.getRole().equalsIgnoreCase("PUBLISHER")) {
         boolean valid = dto.getStudioName() != null && !dto.getStudioName().isBlank()
                 && dto.getPaymentMethod() != null
                 && dto.getAccountName() != null && !dto.getAccountName().isBlank()
                 && dto.getAccountNumber() != null && !dto.getAccountNumber().isBlank()
                 && dto.getBankName() != null && !dto.getBankName().isBlank();

         if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Các trường của Publisher (studioName, paymentMethod, accountName, accountNumber, bankName) không được để trống")
                    .addConstraintViolation();
         }
         return valid;
      }
      return true; // Customer không cần check
   }
}
