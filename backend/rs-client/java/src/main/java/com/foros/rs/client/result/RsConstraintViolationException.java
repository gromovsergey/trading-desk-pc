package com.foros.rs.client.result;

import com.foros.rs.client.model.ConstraintViolation;
import com.foros.rs.client.RsException;

import java.util.List;

public class RsConstraintViolationException extends RsException {

    private List<ConstraintViolation> constraintViolations;

    public RsConstraintViolationException(List<ConstraintViolation> constraintViolations) {
        super(generateMessage(constraintViolations));
        this.constraintViolations = constraintViolations;
    }

    public List<ConstraintViolation> getConstraintViolations() {
        return constraintViolations;
    }

    private static String generateMessage(List<ConstraintViolation> constraintViolations) {
        StringBuilder builder = new StringBuilder();
        builder.append("Response has a constraint violations (").append(constraintViolations.size()).append(")\n");

        int index = 0;
        for (ConstraintViolation constraintViolation : constraintViolations) {
            builder.append("\t").append(++index)
                    .append(". Value:").append(constraintViolation.getValue())
                    .append(". [path:").append(constraintViolation.getPath())
                    .append("]: Code ").append(constraintViolation.getCode()).append(". ").append(constraintViolation.getMessage())
                    .append("\n");
        }
        return builder.toString();
    }
}
