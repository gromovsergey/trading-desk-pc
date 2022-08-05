package com.foros.validation;

import com.foros.validation.constraint.validator.Validator;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.ConstraintViolationFactory;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.strategy.ValidationStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ValidationContextImpl implements ValidationContext, ValidationContextContainer {

    private List<ValidationContext> subContexts = new ArrayList<ValidationContext>();

    private Set<ConstraintViolation> constraintViolations = new LinkedHashSet<ConstraintViolation>();

    private List<ConstraintViolationBuilder> constraintViolationBuilders = new LinkedList<ConstraintViolationBuilder>();

    private Path path;
    private Object bean;

    private ConstraintViolationFactory constraintViolationFactory;

    private ValidationMode validationMode;

    private ValidationStrategy contextStrategy;

    private ValidationStrategy additionalStrategy;

    private boolean flushed = true;
    private boolean incomplete = false;

    public ValidationContextImpl(ConstraintViolationFactory constraintViolationFactory,
                                   ValidationMode validationMode, ValidationStrategy additionalStrategy,
                                   Path path, Object bean) {

        this.constraintViolationFactory = constraintViolationFactory;

        this.validationMode = validationMode;
        this.additionalStrategy = additionalStrategy;

        this.contextStrategy = ValidationStrategies.chain(
                ValidationStrategies.byValidationMode(validationMode),
                additionalStrategy
        );

        this.path = path != null ? path : Path.empty();

        this.bean = bean;
    }

    @Override
    public Set<ConstraintViolation> getConstraintViolations() {
        flush();
        return Collections.unmodifiableSet(constraintViolations);
    }

    @Override
    public ValidationContextBuilder subContext(Object bean) {
        return new ValidationContextBuilderImpl(constraintViolationFactory, bean, path)
                .registerContextContainer(this)
                .withAdditionalStrategy(additionalStrategy);
    }

    @Override
    public ValidationContext createSubContext(Object bean, String subNode, int index) {
        return subContext(bean).withPath(subNode).withIndex(index).build();
    }

    @Override
    public ValidationContext createSubContext(Object bean, String node) {
        return subContext(bean).withPath(node).build();
    }

    @Override
    public ValidationContext createSubContext(Object bean) {
        return subContext(bean).build();
    }

    @Override
    public ValidationContext createSubContext() {
        return subContext(bean).build();
    }

    @Override
    public void registerSubContext(ValidationContext validationContext) {
        subContexts.add(validationContext);
        flushed = false;
    }

    private void flush() {
        if (flushed) {
            return;
        }

        for (ConstraintViolationBuilder builder : constraintViolationBuilders) {
            constraintViolations.add(constraintViolationFactory.create(builder));
        }

        constraintViolationBuilders.clear();

        for (ValidationContext subContext : subContexts) {
            constraintViolations.addAll(subContext.getConstraintViolations());
            incomplete = incomplete || !subContext.isValidationComplete();
        }

        subContexts.clear();

        flushed = true;
    }

    @Override
    public ConstraintViolationBuilder addConstraintViolation(String template) {
        ConstraintViolationBuilder constraintViolationBuilder
                = new ConstraintViolationBuilder(template, path);

        constraintViolationBuilders.add(constraintViolationBuilder);

        flushed = false;

        return constraintViolationBuilder;
    }

    @Override
    public <V, T extends Validator<V, T>> T validator(Class<T> validatorClass) {
        try {
            return validatorClass
                    .newInstance()
                    .withContext(this);

        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    @Override
    public <V, T extends Validator<V, T>> T validator(T validator) {
        return validator.withContext(this);
    }

    @Override
    public boolean isReachable(String property) {
        return contextStrategy.isReachable(this, property);
    }

    @Override
    public PropertyReachableChecker props(String... properties) {
        return new PropertyReachableChecker(this, properties);
    }

    @Override
    public void addConstraintViolations(Collection<ConstraintViolation> violations) {
        constraintViolations.addAll(violations);
    }

    @Override
    public void throwIfHasViolations() throws ConstraintViolationException {
        if (hasViolations()) {
            throw new ConstraintViolationException(getConstraintViolations());
        }
    }

    @Override
    public void throwIfHasViolations(String message) throws ConstraintViolationException {
        if (hasViolations()) {
            throw new ConstraintViolationException(message, getConstraintViolations());
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public ValidationMode getMode() {
        return validationMode;
    }

    @Override
    public Object getBean() {
        if (bean == null) {
            throw new IllegalStateException("Bean is not initialized properly");
        }
        return bean;
    }

    @Override
    public boolean hasViolations() {
        flush();
        return !constraintViolations.isEmpty() || !constraintViolationBuilders.isEmpty();
    }

    @Override
    public boolean ok() {
        return !hasViolations() && isValidationComplete();
    }

    @Override
    public boolean hasViolation(final String property) {
        flush();

        String examinedPropertyPath = property;
        for (ConstraintViolation violation : constraintViolations) {
            if (path != null) {
                examinedPropertyPath = path.add(property).toString();
            }

            Path violationPropertyPath = violation.getPropertyPath();

            if (violationPropertyPath != null && (violationPropertyPath.toString().equals(examinedPropertyPath)
                    || violationPropertyPath.toString().startsWith(examinedPropertyPath+".")
                    || violationPropertyPath.toString().startsWith(examinedPropertyPath+"["))) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setValidationIncomplete() {
        incomplete = true;
    }

    @Override
    public boolean isValidationComplete() {
        flush();
        return !incomplete;
    }

    @Override
    public String toString() {
        return "ValidationContext " + " path=" + path + ", validationMode=" + validationMode;
    }
}
