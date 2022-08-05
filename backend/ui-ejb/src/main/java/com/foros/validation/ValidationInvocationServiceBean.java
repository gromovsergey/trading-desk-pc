package com.foros.validation;

import com.foros.aspect.annotation.ElFunction;
import com.foros.aspect.el.Expression;
import com.foros.aspect.el.ExpressionException;
import com.foros.aspect.el.ExpressionLanguageService;
import com.foros.aspect.registry.AspectDescriptor;
import com.foros.aspect.registry.AspectDescriptorFactoryService;
import com.foros.aspect.registry.AspectRegistry;
import com.foros.aspect.registry.DynamicAspectRegistry;
import com.foros.aspect.registry.ElAspectDescriptor;
import com.foros.aspect.util.MethodUtil;
import com.foros.validation.annotation.Validation;
import com.foros.validation.constraint.violation.ConstraintViolation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "ValidationInvocationService")
public class ValidationInvocationServiceBean implements ValidationInvocationService {

    @EJB
    private AspectRegistry globalAspectRegistry;

    private AspectRegistry dynamicAspectRegistry = new DynamicAspectRegistry() {
        @Override
        protected AspectDescriptorFactoryService getAspectDescriptorFactoryService() {
            return aspectDescriptorFactoryService;
        }
    };

    @EJB
    private AspectDescriptorFactoryService aspectDescriptorFactoryService;

    @EJB
    private ExpressionLanguageService elService;

    @EJB
    private ValidationService validationService;

    @Override
    public void validate(Object target, Method method, Object[] params) {
        new Validator(target, method, params, globalAspectRegistry)
                .invoke()
                .throwIfHasViolations();
    }

    @Override
    public Set<ConstraintViolation> validateWeb(Object target, Method method) {
        return new Validator(target, method, null, dynamicAspectRegistry)
                .invoke()
                .getConstraintViolations();
    }

    private class Validator {
        private Object target;
        private Method method;
        private Object[] params;
        private AspectDescriptor descriptor;
        private AspectRegistry aspectRegistry;
        private ValidationContext validationContext;

        public Validator(Object target, Method method, Object[] params, AspectRegistry aspectRegistry) {
            this.target = target;
            this.method = method;
            this.params = params;
            this.aspectRegistry = aspectRegistry;
        }

        private Validator invoke() {
            descriptor = aspectRegistry.getDescriptor(Validation.class, method);

            if (descriptor == null || !(descriptor instanceof ElAspectDescriptor)) {
                return this;
            }

            Map<String, Object> context = MethodUtil.createParametersMap(target, method, params);

            validateImpl((ElAspectDescriptor)descriptor, context);

            return this;
        }

        private void validateImpl(ElAspectDescriptor descriptor, Map<String, Object> context) {
            Object[] parameters = new Object[descriptor.getParameterExpressions().length];

            for (int i = 0; i < descriptor.getParameterExpressions().length; i++) {
                Expression expression = descriptor.getParameterExpressions()[i];

                try {
                    parameters[i] = elService.evaluate(expression, context, ElFunction.Namespace.GENERAL);
                } catch (ExpressionException e) {
                    throw new IllegalArgumentException(e);
                }
            }

            validationContext = validationService.validate(descriptor.getName(), parameters);
        }

        public void throwIfHasViolations() {
            if (validationContext != null) {
                validationContext
                        .throwIfHasViolations("Validation failed: " + (descriptor == null ? "" : descriptor));
            }
        }

        public Set<ConstraintViolation> getConstraintViolations() {
            return validationContext == null ? Collections.<ConstraintViolation>emptySet()
                    : validationContext.getConstraintViolations();
        }
    }
}
