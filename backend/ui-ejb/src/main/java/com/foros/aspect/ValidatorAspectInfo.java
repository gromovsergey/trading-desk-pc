package com.foros.aspect;

import com.foros.validation.constraint.validator.ValidatorFactory;
import java.lang.annotation.Annotation;

public class ValidatorAspectInfo extends AbstractAspectInfo {

    private Class<? extends ValidatorFactory<?, ?, ?>> fieldValidator;

    public ValidatorAspectInfo(Annotation annotation, Class<? extends Annotation> index,
                               Class<? extends ValidatorFactory<?, ?, ?>> fieldValidator) {
        super(annotation, index);
        this.fieldValidator = fieldValidator;
    }

    public Class<? extends ValidatorFactory<?, ?, ?>> getValidatorFactoryType() {
        return fieldValidator;
    }

    @Override
    public boolean forProperty() {
        return true;
    }

}
