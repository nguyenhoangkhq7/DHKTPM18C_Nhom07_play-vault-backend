package fit.iuh.validation;

import fit.iuh.models.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {
   @Override
   public boolean isValid(String value, ConstraintValidatorContext context) {
      if (value == null) return false;
      try {
         Role.valueOf(value.toUpperCase());
         return true;
      } catch (IllegalArgumentException e) {
         return false;
      }
   }
}

