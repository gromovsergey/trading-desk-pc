package com.foros.validation.constraint.violation;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationException;
import com.foros.validation.code.ForosError;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;
import com.foros.validation.util.ValidationUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConstraintViolationException extends ValidationException {

    private Set<ConstraintViolation> constraintViolations;

    public ConstraintViolationException(String message, Set<ConstraintViolation> constraintViolations) {
        super(message + "\n" + generateViolationsInfo(constraintViolations));
        this.constraintViolations = constraintViolations;
    }

    public ConstraintViolationException(Set<ConstraintViolation> constraintViolations) {
        this("Validation failed. With violations.", constraintViolations);
    }

    public ConstraintViolationException(Set<ConstraintViolation> constraintViolations, Throwable cause) {
        super("Validation failed. With violations.\n" + generateViolationsInfo(constraintViolations), cause);
        this.constraintViolations = constraintViolations;
    }

    public ConstraintViolationException(ForosError error, String messageTemplate) {
        super(StringUtil.getLocalizedString(messageTemplate));
        this.constraintViolations = new HashSet<>();
        this.constraintViolations.add(new ConstraintViolationImpl(error, this.getMessage(), Path.empty(), null, messageTemplate));
    }

    public Set<ConstraintViolation> getConstraintViolations() {
        return constraintViolations;
    }

    private static String generateViolationsInfo(Set<ConstraintViolation> constraintViolations) {
        StringBuilder builder = new StringBuilder("Constraint violations:\n");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            builder.append("\t-").append(constraintViolation.toString()).append("\n");
        }

        return builder.toString();
    }

    public static Builder newBuilder(String template) {
        return new Builder(template, Path.empty());
    }

    public static class Builder extends ConstraintViolationBuilder<Builder> {
        public Builder(String template, Path basePath) {
            super(template, basePath);
        }

        public ConstraintViolationException build() {
            StringUtilsMessageInterpolator interpolator =
                    new StringUtilsMessageInterpolator(CurrentUserSettingsHolder.getLocale());

            ConstraintViolation cv = build(ValidationUtil.getDefaultCodesResolver(), interpolator);
            return new ConstraintViolationException(Collections.singleton(cv));
        }
    }
}
