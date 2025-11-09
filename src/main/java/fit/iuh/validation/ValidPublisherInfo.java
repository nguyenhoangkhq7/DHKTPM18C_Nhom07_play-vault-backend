package fit.iuh.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PublisherInfoValidator.class)
@Documented
public @interface ValidPublisherInfo {
   String message() default "Thông tin Publisher không hợp lệ";
   Class<?>[] groups() default {};
   Class<? extends Payload>[] payload() default {};
}
