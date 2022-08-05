package com.foros.validation;

import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.strategy.SubContextSupportStrategy;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.strategy.ValidationStrategy;

public class ValidationContextBuilderImpl implements ValidationContextBuilder {

    private ValidationContextContainer container;

    private ConstraintViolationFactory constraintViolationFactory;

    private Object bean;
    private Path path;

    private String node = "";

    private Integer index = null;

    private ValidationStrategy additionalStrategy = ValidationStrategies.defaultStrategy();

    private ValidationMode mode = ValidationMode.DEFAULT;

    public ValidationContextBuilderImpl(
            ConstraintViolationFactory constraintViolationFactory,
            Object bean, Path path) {

        this.constraintViolationFactory = constraintViolationFactory;

        this.bean = bean;
        this.path = path;
    }

    public ValidationContextBuilder registerContextContainer(ValidationContextContainer contextContainer) {
        this.container = contextContainer;
        return this;
    }

    @Override
    public ValidationContextBuilder withPath(String path) {
        this.node = path;
        return this;
    }

    @Override
    public ValidationContextBuilder withIndex(int index) {
        this.index = index;
        return this;
    }

    @Override
    public ValidationContextBuilder withMode(ValidationMode mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public ValidationContextBuilder withAdditionalStrategy(ValidationStrategy strategy) {
        this.additionalStrategy = strategy;
        return this;
    }

    @Override
    public ValidationContext build() {

        Path childPath = path.add(node, index);

        ValidationContext subContext =
                new ValidationContextImpl(constraintViolationFactory, mode, getAdditionalStrategy(childPath), childPath, bean);

        if (container != null) {
            container.registerSubContext(subContext);
        }

        return subContext;
    }

    private ValidationStrategy getAdditionalStrategy(Path childPath) {
        if (additionalStrategy instanceof SubContextSupportStrategy && !childPath.isEmpty()) {
            SubContextSupportStrategy subContextSupport = (SubContextSupportStrategy) additionalStrategy;
            return subContextSupport.subContextStrategy(childPath);
        }
        return additionalStrategy;
    }

}
