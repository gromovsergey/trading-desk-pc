package com.foros.validation.bean;

import com.foros.aspect.annotation.ElFunction;
import com.foros.aspect.el.Expression;
import com.foros.aspect.el.ExpressionException;
import com.foros.aspect.el.ExpressionLanguageService;
import com.foros.aspect.registry.AspectDescriptor;
import com.foros.aspect.registry.AspectRegistry;
import com.foros.aspect.registry.ElAspectDescriptor;
import com.foros.aspect.registry.PropertyDescriptor;
import com.foros.aspect.registry.ValidatorAspectDescriptor;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validation;
import com.foros.validation.constraint.validator.Validator;
import com.foros.validation.constraint.validator.ValidatorFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "BeansValidationService")
public class BeansValidationServiceBean implements BeansValidationService {

    @EJB
    private AspectRegistry aspectRegistry;

    @EJB
    private ValidationService validationService;

    @EJB
    private ExpressionLanguageService elService;

    @Override
    public void validate(ValidationContext context) {
        traverse(context);
    }

    private void traverse(ValidationContext context) {
        Object bean = context.getBean();

        Map<String, Set<PropertyDescriptor>> propertyDescriptors
                = aspectRegistry.getPropertyDescriptors(Validation.class, bean.getClass());

        if (propertyDescriptors == null) {
            return;
        }

        for (Map.Entry<String, Set<PropertyDescriptor>> entry : propertyDescriptors.entrySet()) {
            String property = entry.getKey();
            Set<PropertyDescriptor> descriptors = entry.getValue();

            for (PropertyDescriptor descriptor : descriptors) {
                if (isReachable(context, descriptor)) {
                    onAspect(context, descriptor);
                    if (isNeedDeeper(descriptor)) {
                        Object value = descriptor.getValue(bean);
                        String name = descriptor.getProperty();

                        ValidationContext subContext = context.createSubContext(value, name);
                        traverse(subContext);
                    }
                }
            }
        }
    }

    private Map<String, Object> createElContext(PropertyDescriptor propertyDescriptor, Object bean) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("annotation", propertyDescriptor.getAspectDescriptor().getAnnotation());
        context.put("fieldName", propertyDescriptor.getProperty());
        context.put("value", propertyDescriptor.getValue(bean));
        context.put("this", bean);
        return context;
    }

    private Object evaluate(Expression expression, Map<String, Object> context) {
        try {
            return elService.evaluate(expression, context, ElFunction.Namespace.GENERAL);
        } catch (ExpressionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void onAspect(ValidationContext context, PropertyDescriptor propertyDescriptor) {
        AspectDescriptor aspectDescriptor = propertyDescriptor.getAspectDescriptor();

        if (aspectDescriptor instanceof ElAspectDescriptor) {
            validateByAspect(context, propertyDescriptor, (ElAspectDescriptor) aspectDescriptor);
        } else if (aspectDescriptor instanceof ValidatorAspectDescriptor) {
            ValidatorAspectDescriptor validatorAspectDescriptor = (ValidatorAspectDescriptor) aspectDescriptor;
            ValidatorFactory validatorFactory = validatorAspectDescriptor.getValidatorFactory();
            validateByValidator(context, propertyDescriptor, aspectDescriptor, validatorFactory);
        }
    }

    private void validateByValidator(ValidationContext context, PropertyDescriptor propertyDescriptor,
                                     AspectDescriptor aspectDescriptor, ValidatorFactory validatorFactory) {

        String name = propertyDescriptor.getProperty();
        Object value = propertyDescriptor.getValue(context.getBean());

        Annotation annotation = aspectDescriptor.getAnnotation();
        Validator validator = validatorFactory.validator(annotation).withContext(context).withPath(name);
        validator.validate(value);
    }

    private void validateByAspect(ValidationContext context, PropertyDescriptor propertyDescriptor, ElAspectDescriptor aspectDescriptor) {
        Object[] parameters = new Object[aspectDescriptor.getParameterExpressions().length];
        Map<String, Object> elContext = createElContext(propertyDescriptor, context.getBean());

        int index = 0;
        for (Expression expression : aspectDescriptor.getParameterExpressions()) {
            parameters[index] = evaluate(expression, elContext);
            index++;
        }

        validationService.validateWithContext(context, aspectDescriptor.getName(), parameters);
    }

    private boolean isNeedDeeper(PropertyDescriptor propertyDescriptor) {
        return false; // todo!!!
    }

    private boolean isReachable(ValidationContext context, PropertyDescriptor propertyDescriptor) {
        return context.isReachable(propertyDescriptor.getProperty());
    }

}
