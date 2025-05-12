package com.example.taskservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Enum<?>> {

    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Allow null; use @NotNull if required
        }

        // Check if the value's class matches the expected enum class
        if (!enumClass.isAssignableFrom(value.getClass())) {
            return false;
        }

        try {
            // Verify the value is a valid constant of the specified enum class
            for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
                if (enumConstant.name().equals(value.name())) {
                    return true;
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            // Handle invalid enum value
            return false;
        }
    }
}