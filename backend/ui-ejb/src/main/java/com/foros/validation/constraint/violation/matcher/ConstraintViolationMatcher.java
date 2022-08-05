package com.foros.validation.constraint.violation.matcher;

import com.foros.validation.constraint.violation.ConstraintViolation;
import java.util.regex.Matcher;

public class ConstraintViolationMatcher {
    private Matcher matcher;
    private ConstraintViolation constraintViolation;

    public ConstraintViolationMatcher(Matcher matcher, ConstraintViolation constraintViolation) {
        this.matcher = matcher;
        this.constraintViolation = constraintViolation;
    }

    public boolean matches() {
        return matcher.matches();
    }

    public String[] groups() {
        String[] groups = new String[matcher.groupCount()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = matcher.group(i + 1);
        }
        return groups;
    }

    public String group(int i) {
        return matcher.group(i + 1);
    }

    public int groupsCount() {
        return matcher.groupCount();
    }

    public ConstraintViolation getConstraintViolation() {
        return constraintViolation;
    }
}
