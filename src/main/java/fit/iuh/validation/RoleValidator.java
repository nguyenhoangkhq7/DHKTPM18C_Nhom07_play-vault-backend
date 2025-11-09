package fit.iuh.validation;

import fit.iuh.models.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RoleValidator implements ConstraintValidator<ValidRole, Role> {
   @Override
   public boolean isValid(Role value, ConstraintValidatorContext context) {
      return value != null;
   }
}


