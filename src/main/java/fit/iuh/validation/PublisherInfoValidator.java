package fit.iuh.validation;

import fit.iuh.dtos.PublisherRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils; // Import cái này để check rỗng

public class PublisherInfoValidator implements ConstraintValidator<ValidPublisherInfo, PublisherRegisterRequest> {

   @Override
   public boolean isValid(PublisherRegisterRequest dto, ConstraintValidatorContext context) {
      // --- XÓA BỎ DÒNG IF CHECK ROLE ---
      // Lý do: Validator này gắn trực tiếp vào PublisherRegisterRequest rồi,
      // nên mặc định nó là Publisher, không cần check lại getRole() làm gì cho lỗi.

      if (dto == null) return true; // Null check an toàn

      // Logic kiểm tra: Nếu là Publisher thì các trường này không được rỗng
      boolean isValid = StringUtils.hasText(dto.getStudioName())
              && StringUtils.hasText(dto.getPaymentMethod().toString()) // Enum check
              && StringUtils.hasText(dto.getAccountName())
              && StringUtils.hasText(dto.getAccountNumber())
              && StringUtils.hasText(dto.getBankName());

      if (!isValid) {
         context.disableDefaultConstraintViolation();
         context.buildConstraintViolationWithTemplate("Các trường của Publisher (Studio, Payment...) không được để trống")
                 .addConstraintViolation();
      }

      return isValid;
   }
}