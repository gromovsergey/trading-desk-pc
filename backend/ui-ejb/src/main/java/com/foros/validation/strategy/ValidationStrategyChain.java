package com.foros.validation.strategy;

import com.foros.validation.ValidationContext;
import com.foros.validation.constraint.violation.Path;

import java.util.ArrayList;
import java.util.List;

public class ValidationStrategyChain implements SubContextSupportStrategy {

    private List<ValidationStrategy> conditions = new ArrayList<ValidationStrategy>();

    public ValidationStrategyChain add(ValidationStrategy condition) {
        if (condition != null) {
            conditions.add(condition);
        }

        return this;
    }

    @Override
    public boolean isReachable(ValidationContext context, String fieldName) {
        for (ValidationStrategy condition : conditions) {
            if (!condition.isReachable(context, fieldName)) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return conditions.isEmpty();
    }

    @Override
    public ValidationStrategyChain subContextStrategy(Path path) {
        ValidationStrategyChain chain = new ValidationStrategyChain();
        for (ValidationStrategy condition : conditions) {
            if (condition instanceof SubContextSupportStrategy) {
                SubContextSupportStrategy subContextSupportLink = (SubContextSupportStrategy) condition;
                chain.add(subContextSupportLink.subContextStrategy(path));

            } else {
                chain.add(condition);
            }
        }
        return chain;
    }
}
