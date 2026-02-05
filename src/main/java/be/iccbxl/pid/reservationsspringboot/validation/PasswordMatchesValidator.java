package be.iccbxl.pid.reservationsspringboot.validation;

import be.iccbxl.pid.reservationsspringboot.dto.UserRegistrationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDto> {

    @Override
    public boolean isValid(UserRegistrationDto dto, ConstraintValidatorContext context) {
        String pwd = dto.getPassword();
        String confirm = dto.getConfirmPassword();

        // Si les deux champs sont vides, on considère que le mot de passe n’est pas modifié => OK
        if ((pwd == null || pwd.isBlank()) && (confirm == null || confirm.isBlank())) {
            return true;
        }

        // Sinon, il faut qu’ils soient identiques
        if (pwd == null) return false;
        return pwd.equals(confirm);
    }
}
