package com.foros.session;

import com.foros.model.EntityBase;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.AbstractPersistentContextSupportValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class BeanValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager entityManager;

    @EJB
    private BeansValidationService beansValidationService;

    public <V, T extends AbstractPersistentContextSupportValidator<V, T>> T persistentValidator(ValidationContext context, Class<T> validatorType) {
        return context.validator(validatorType)
                .withPersistentContext(entityManager);
    }

    public <V extends EntityBase> LinkValidator<V> linkValidator(ValidationContext context, Class<V> entityType) {
        return persistentValidator(context, LinkValidator.forClass(entityType))
                .withClass(entityType);
    }

    @Validation
    public void validateBean(ValidationContext context, String filedName, Object bean, ValidationMode validationMode) {
        if (bean instanceof Iterable) {
            Iterable iterable = (Iterable) bean;
            int index = 0;
            for (Object item : iterable) {
                if (item != null) {
                    beansValidationService.validate(
                            context.subContext(item)
                                    .withPath(filedName)
                                    .withIndex(index)
                                    .withMode(validationMode)
                                    .build()
                    );
                }
                index++;
            }
        } else if (bean != null) {
            beansValidationService.validate(
                    context.subContext(bean)
                            .withPath(filedName)
                            .withMode(validationMode)
                            .build()
            );
        }
    }

    @Validation
    public void validateContext(ValidationContext context) {
        beansValidationService.validate(context);
    }
}
