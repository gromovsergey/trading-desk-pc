package com.foros.validation.constraint.violation.parsing;

import com.foros.model.EntityBase;
import com.foros.model.ExtensionProperty;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import java.util.Collection;
import java.util.HashSet;

public final class ParseErrorsSupport {

    public static final ExtensionProperty<ParseErrorsContainer> PARSE_ERRORS =
            new ExtensionProperty<ParseErrorsContainer>(ParseErrorsContainer.class);

    private ParseErrorsSupport() {
    }

    public static void addErrors(EntityBase entity, Collection<ConstraintViolation> errors) {
        if (errors != null && !errors.isEmpty()) {
            ParseErrorsContainer container = entity.getProperty(PARSE_ERRORS);
            if (container == null) {
                container = new ParseErrorsContainer();
                entity.setProperty(PARSE_ERRORS, container);
            }
            container.addErrors(errors);
        }
    }

    public static void throwIfAnyErrorsPresent(Collection<ConstraintViolation> errors)
            throws ConstraintViolationException {
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation>(errors));
        }
    }

    public static void throwIfAnyErrorsPresent(ParseErrorsContainer container)
            throws ConstraintViolationException {
        throwIfAnyErrorsPresent(container.getErrors());
    }
}
