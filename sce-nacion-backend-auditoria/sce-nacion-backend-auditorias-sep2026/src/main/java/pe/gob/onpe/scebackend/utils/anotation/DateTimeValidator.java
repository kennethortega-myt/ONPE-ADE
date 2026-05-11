package pe.gob.onpe.scebackend.utils.anotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateTimeValidator implements ConstraintValidator<Alphanumeric, String> {

    private String regexp;

    @Override
    public void initialize(Alphanumeric constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null values; use @NotNull for null checks
        }
        return value.matches(regexp);
    }
}