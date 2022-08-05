package com.foros.validation.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationContextBuilder;
import com.foros.validation.ValidationContextBuilderImpl;
import com.foros.validation.code.ResourceKeyErrorResolver;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.interpolator.ForosErrorResolver;
import com.foros.validation.interpolator.StringUtilsMessageInterpolator;

import java.io.Serializable;
import java.util.Locale;

public final class ValidationUtil {
    private static final Object ROOT = new Serializable() {};

    private static final ForosErrorResolver CODES_MESSAGE_INTERPOLATOR = ResourceKeyErrorResolver.INSTANCE;


    private ValidationUtil() {
    }

    /**
     * Create context builder for root validation context
     *
     * @param root context validation object
     * @return validation context builder
     */
    public static ValidationContextBuilder validationContext(Object root) {
        Locale locale = CurrentUserSettingsHolder.getLocaleOrDefault();
        StringUtilsMessageInterpolator interpolator = new StringUtilsMessageInterpolator(locale);
        ConstraintViolationFactory factory = new ConstraintViolationFactory(CODES_MESSAGE_INTERPOLATOR, interpolator);
        return new ValidationContextBuilderImpl(factory, root, Path.empty());
    }

    /**
     * @return validation context builder with default root object,
     * used for all automatic validations as root context
     */
    public static ValidationContextBuilder validationContext() {
        return validationContext(ROOT);
    }

    public static ValidationContext createContext() {
        return validationContext().build();
    }

    public static ForosErrorResolver getDefaultCodesResolver() {
        return CODES_MESSAGE_INTERPOLATOR;
    }
}
