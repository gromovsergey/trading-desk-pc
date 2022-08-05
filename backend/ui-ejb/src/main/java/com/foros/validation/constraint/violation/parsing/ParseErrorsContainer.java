package com.foros.validation.constraint.violation.parsing;

import com.foros.validation.constraint.violation.ConstraintViolation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ParseErrorsContainer implements Serializable {

    private Collection<ConstraintViolation> errors = new ArrayList<ConstraintViolation>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Collection<ConstraintViolation> getErrors() {
        return Collections.unmodifiableCollection(errors);
    }

    public void addErrors(Collection<ConstraintViolation> errors) {
        this.errors.addAll(errors);
    }
}
