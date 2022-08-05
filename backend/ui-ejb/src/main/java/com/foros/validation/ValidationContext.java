package com.foros.validation;

import com.foros.validation.constraint.validator.Validator;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationBuilder;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.Path;
import com.foros.validation.strategy.ValidationMode;

import java.util.Collection;
import java.util.Set;

/**
 * Context of validation. Context can not be changed. You can add constraint violation,
 * set validation incomplete or create subcontext for validations with another parameters.
 */
public interface ValidationContext {

    /**
     * @return set of collected constraint violations
     */
    Set<ConstraintViolation> getConstraintViolations();

    /**
     * <p>Add constraint violation in context using builder</p>
     *
     * <p>Usage:</p>
     * <blockquote><pre>
     *     ValidationContext context = ...;
     *     context
     *          .addConstraintViolation("some.resource.name")
     *          .withPath("fieldName")
     *          .withParameters(100, 200)
     *          .withValue(validatedValue);
     * </pre></blockquote>
     *
     * Builder has no <code>build</code> method, constraint violation add in context automatically.
     * See {@link ConstraintViolationBuilder} for delails.
     *
     * @param template message template
     * @return builder for filling constraint violation
     */
    ConstraintViolationBuilder addConstraintViolation(String template);

    /**
     * <p>Create sub-context using bulder</p>
     *
     * <p>Usage:</p>
     * <blockquote><pre>
     *     SomeBean bean = ...;
     *     ValidationContext context = ...;
     *
     *     ValidationContext subContext = context
     *          .subContext(someBean.getChild())
     *          .withPath("child")
     *          .withMode(ValidationMode.UPDATE)
     *          .build();
     *
     *     validateChild(subContext);
     * </pre></blockquote>
     *
     * @param bean validation context value, {@link com.foros.validation.ValidationContext#getBean()}
     * @return bulder for validation context construction. See {@link ConstraintViolationBuilder} for details.
     */
    ValidationContextBuilder subContext(Object bean);

    /**
     * <p>Create sub-context with default parameters with same validation value.</p>
     *
     * @return constructed sub-context
     */
    ValidationContext createSubContext();

    /**
     * <p>Create sub-context with default parameters with different validation value.
     * Alias for <code>subContext(bean).build()</code></p>
     *
     * @param bean validation value
     * @return constructed sub-context
     */
    ValidationContext createSubContext(Object bean);

    /**
     * <p>Create sub-context with default parameters with different validation value and additional path.
     * Alias for <code>subContext(bean).withPath(node).build()</code></p>
     *
     * @param bean validation value
     * @param node additional sub-context path
     * @return constructed sub-context
     */
    ValidationContext createSubContext(Object bean, String node);

    /**
     * <p>Create sub-context with default parameters with different validation value and additional indexed path.
     * Alias for <code>subContext(bean).withPath(node).withIndex(index).build()</code></p>
     *
     * @param bean validation value
     * @param node additional sub-context path
     * @param index item index
     * @return constructed sub-context
     */
    ValidationContext createSubContext(Object bean, String node, int index);

    /**
     * <p>Create validator by class.</p>
     *
     * <p>Usage:</p>
     * <blockquote><pre>
     *     ValidationContext context = ...;
     *
     *     context
     *          .validator(SomeValidator.class)
     *          .withSomeValidatorParam("param")
     *          .validate(value);
     * </pre></blockquote>
     *
     * @param validatorClass validator class
     * @param <V> type of validating value
     * @param <T> validator type
     * @return new instance of validator associated with current context. See {@link Validator} for details.
     */
    <V, T extends Validator<V, T>> T validator(Class<T> validatorClass);

    /**
     * <p>Associate validator with current context</p>
     *
     * @param validator validator instance
     * @param <V> type of validating value
     * @param <T> validator type
     * @return passed instance of validator associated with current context
     */
    <V, T extends Validator<V, T>> T validator(T validator);

    /**
     * Set up validation incomplete flag. If validation incomplete, context must have constraint
     * violations, else throws ValidationException.
     */
    void setValidationIncomplete();

    /**
     * @return is incomplete flag set up?
     */
    boolean isValidationComplete();

    /**
     * @return context validation value
     */
    Object getBean();

    /**
     * @return validation path. See {@link Path} for details.
     */
    Path getPath();

    /**
     * Validation mode set {@link ValidationContext#isReachable} method strategy.
     * Validation mode list and strategy details see {@link ValidationMode}.
     *
     * @return validation mode, {@link ValidationMode#DEFAULT} by default.
     */
    ValidationMode getMode();

    /**
     *
     * @param property propety name
     * @return reachable
     */
    boolean isReachable(String property);

    /**
     * @param property propety name
     * @return is context has constraint violation(s) for passed property?
     */
    boolean hasViolation(String property);

    /**
     * @return is context has constraint violation(s)?
     */
    boolean hasViolations();

    /**
     * @return true if context has no violations and validation incomplete flag not set
     */
    boolean ok();

    /**
     * <p>Use this checker for more complicated checks. See {@link PropertyReachableChecker} for details.</p>
     *
     * <p>Usage:</p>
     * <blockquote><pre>
     *     ValidationContext context = ...;
     *
     *     if(context.props("prop1", "prop2").reachableAndNoViolations()) {
     *         // make validations
     *         ...
     *     }
     *
     *     validateChild(subContext);
     * </pre></blockquote>
     *
     * @param properties property list for checks
     * @return checker with ability to complicated reachable and violations checks
     */
    PropertyReachableChecker props(String... properties);

    void addConstraintViolations(Collection<ConstraintViolation> violations);

    /**
     * @throws ConstraintViolationException if context has constraint violations
     */
    void throwIfHasViolations() throws ConstraintViolationException;

    /**
     * @throws ConstraintViolationException if context has constraint violations
     */
    void throwIfHasViolations(String message) throws ConstraintViolationException;

}
