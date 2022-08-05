package com.foros.aspect.registry;

import com.foros.aspect.ValidatorAspectInfo;
import com.foros.validation.constraint.validator.ValidatorFactory;
import java.lang.annotation.Annotation;

public class ValidatorAspectDescriptor implements AspectDescriptor {

    private ValidatorFactory<?, ?, ?> validatorFactory;
    private ValidatorAspectInfo aspectInfo;

    public ValidatorAspectDescriptor(ValidatorAspectInfo aspectInfo) {
        this.aspectInfo = aspectInfo;
        this.validatorFactory = createValidator(aspectInfo.getValidatorFactoryType());
    }

    private ValidatorFactory<?, ?, ?> createValidator(Class<? extends ValidatorFactory<?, ?, ?>> fieldValidator) {
        try {
            return fieldValidator.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Annotation getAnnotation() {
        return aspectInfo.getAnnotation();
    }

    public ValidatorFactory<?, ?, ?> getValidatorFactory() {
        return validatorFactory;
    }

    @Override
    public String toString() {
        return "ValidatorAspect: validator class " + aspectInfo.getValidatorFactoryType().getName();
    }
}
