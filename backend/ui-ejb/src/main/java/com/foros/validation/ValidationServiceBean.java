package com.foros.validation;

import com.foros.aspect.registry.AspectDeclarationDescriptor;
import com.foros.aspect.registry.AspectDeclarationRegistry;
import com.foros.session.ServiceLocator;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.strategy.ValidationStrategy;
import com.foros.validation.util.ValidationUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless(name = "ValidationService")
public class ValidationServiceBean implements ValidationService {

    @EJB
    private AspectDeclarationRegistry aspectDeclarationRegistry;

    @EJB
    private BeansValidationService beanValidationService;

    @Override
    public ValidationContext validate(String validationName, Object... params) {
        return validate(null, validationName, params);
    }

    @Override
    public ValidationContext validate(ValidationStrategy additionalStrategy, String validationName, Object... params) {
        ValidationContext context = ValidationUtil
                .validationContext()
                .withAdditionalStrategy(additionalStrategy)
                .build();

        validateWithContext(context, validationName, params);

        Set<ConstraintViolation> constraintViolations = context.getConstraintViolations();

        if (constraintViolations.isEmpty() && !context.isValidationComplete()) {
            throw new ValidationException("Incomplete validation without constraint violations");
        }

        return context;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ValidationContext validateInNewTransaction(String validationName, Object... params) {
        return validate(validationName, params);
    }

    @Override
    public ValidationContext validateParameters(Method method, Object[] params) {
        ValidationContext customValidationContext = ValidationUtil.createContext();

        validateParameters(customValidationContext, method, params);

        return customValidationContext;
    }

    private void validateParameters(ValidationContext context, Method method, Object[] params) {
        // todo: cache?
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] types = method.getParameterTypes();

        for (int i = 0, typesLength = types.length; i < typesLength; i++) {
            Annotation[] annotation = parameterAnnotations[i];

            ValidateBean validateBean = findValidateBeanAnnotation(annotation);

            if (validateBean != null) {
                ValidationContextBuilder contextBuilder = context.subContext(params[i]);

                if (validateBean.value() != ValidationMode.DEFAULT) {
                    contextBuilder.withMode(validateBean.value());
                }

                ValidationContext subContext = contextBuilder.build();

                beanValidationService.validate(subContext);
            }
        }
    }

    private ValidateBean findValidateBeanAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ValidateBean) {
                return (ValidateBean) annotation;
            }
        }

        return null;
    }

    @Override
    public void validateWithContext(ValidationContext context, String validationName, Object... params) {
        AspectDeclarationDescriptor declarationDescriptor =
                aspectDeclarationRegistry.getDescriptor(Validation.class, validationName);

        Object[] completeParams = createCompleteParams(params, context);

        Method method = declarationDescriptor.getMethod(completeParams);

        Object service = ServiceLocator.getInstance().lookup(declarationDescriptor.getServiceClass());

        validateImpl(service, method, context, completeParams);
    }

    private void validateImpl(Object service, Method method, ValidationContext context, Object[] params) {
        try {
            // validate method parameters with bean validation
            validateParameters(context, method, params);

            // invoke validation
            method.invoke(service, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ValidationException(e);
        }
    }

    private Object[] createCompleteParams(Object[] params, ValidationContext context) {
        Object[] completeParams = new Object[params.length+1];
        completeParams[0] = context;
        System.arraycopy(params, 0, completeParams, 1, params.length);
        return completeParams;
    }
}
