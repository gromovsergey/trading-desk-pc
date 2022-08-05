package com.foros.framework;

import com.foros.action.ConstraintValidationsAware;
import com.foros.action.Invalidable;
import com.foros.validation.ValidationInvocationService;
import com.foros.validation.constraint.convertion.StrutsConstraintViolationConverter;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import java.lang.reflect.Method;
import java.util.Set;
import javax.ejb.EJB;

public class ValidateAnnotationInterceptor extends ValidationInterceptor {

    @EJB
    private ValidationInvocationService validationInvocationService;

    @Override
    protected void doBeforeInvocation(ActionInvocation invocation) throws Exception {
        try {
            super.doBeforeInvocation(invocation);
        } catch (ConstraintViolationException e) {
            if (invocation.getAction() instanceof ConstraintValidationsAware) {
                ConstraintValidationsAware action = (ConstraintValidationsAware) invocation.getAction();
                addErrors(action, invocation.getStack(), e.getConstraintViolations());
            } else {
                throw e;
            }
        }
        validate(invocation);
    }

    private void validate(ActionInvocation invocation) throws Exception {
        if (!(invocation.getAction() instanceof ConstraintValidationsAware)) {
            return;
        }

        ConstraintValidationsAware action = (ConstraintValidationsAware) invocation.getAction();

        if (!action.hasErrors()) {
            return;
        }

        Class<?> actionClass = action.getClass();
        String methodName = invocation.getProxy().getMethod();
        Method method = actionClass.getMethod(methodName);

        // TODO: skip validation for action.getFieldErrors().keySet()
        Set<ConstraintViolation> violations = validationInvocationService.validateWeb(action, method);

        addErrors(action,invocation.getStack(), violations);

        if (action instanceof Invalidable) {
            ((Invalidable) action).invalid();
        }
    }


    public static void addErrors(ConstraintValidationsAware action, ValueStack stack, Set<ConstraintViolation> violations) {
        if (violations.isEmpty()) {
            return;
        }

        final Set<String> existingFieldErrors = action.getFieldErrors().keySet();

        (new StrutsConstraintViolationConverter(action, stack) {
            @Override
            protected void addError(String path, String message) {
                if (existingFieldErrors.isEmpty()) {
                    super.addError(path, message);
                } else {
                    if (path == null || path.isEmpty()) {
                        // Ignore entity level errors, just in case.
                        return;
                    }
                    for (String fieldError : existingFieldErrors) {
                        if (fieldError.startsWith(path) || path.startsWith(fieldError)) {
                            return;
                        }
                    }
                    super.addError(path, message);
                }
            }
        }).applyRules(action.getConstraintViolationRules(), violations);
        action.getConstraintViolations().addAll(violations);
    }
}
