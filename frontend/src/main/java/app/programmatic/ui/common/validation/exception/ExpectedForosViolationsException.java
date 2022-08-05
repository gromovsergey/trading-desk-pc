package app.programmatic.ui.common.validation.exception;

import java.util.Map;
import java.util.Set;

public class ExpectedForosViolationsException extends RuntimeException {
    private Map<String, Set<String>> violationDescriptions;

    public ExpectedForosViolationsException(Map<String, Set<String>> violationDescriptions) {
        this.violationDescriptions = violationDescriptions;
    }

    public Map<String, Set<String>> getViolationDescriptions() {
        return violationDescriptions;
    }
}
