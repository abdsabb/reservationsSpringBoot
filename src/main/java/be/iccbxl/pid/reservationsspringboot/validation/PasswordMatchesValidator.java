package be.iccbxl.pid.reservationsspringboot.validation;

import be.iccbxl.pid.reservationsspringboot.dto.UserRegistrationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegistrationDto> {

    @Override
    public boolean isValid(UserRegistrationDto dto, ConstraintValidatorContext context) {
        if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
            return false;
        }
        return dto.getPassword().equals(dto.getConfirmPassword());
    }
}
