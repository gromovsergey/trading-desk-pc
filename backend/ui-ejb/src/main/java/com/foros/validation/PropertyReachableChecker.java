package com.foros.validation;

public class PropertyReachableChecker {
    private ValidationContext validationContext;

    private String[] properties;
    private boolean checkAny = true;

    PropertyReachableChecker(ValidationContext validationContext, String[] properties) {
        this.validationContext = validationContext;
        this.properties = properties;
    }

    public boolean haveViolations() {
        return !noViolations();
    }

    public boolean noViolations() {
        for (String property : properties) {
            if (validationContext.hasViolation(property)) {
                return false;
            }
        }

        return true;
    }

    public boolean reachable() {
        for (String property : properties) {
            if (checkAny) {
                if (validationContext.isReachable(property)) {
                    return true;
                }
            } else {
                if (!validationContext.isReachable(property)) {
                    return false;
                }
            }
        }

        return !checkAny;
    }

    public boolean reachableAndNoViolations() {
        return noViolations() && reachable();
    }

    public PropertyReachableChecker any() {
        this.checkAny = true;
        return this;
    }

    public PropertyReachableChecker all() {
        this.checkAny = false;
        return this;
    }

}
