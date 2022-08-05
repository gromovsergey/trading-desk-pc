package com.foros.framework.struts;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;

public class AnnotationActionValidatorManager extends com.opensymphony.xwork2.validator.AnnotationActionValidatorManager {

    @Override
    public void validate(Object object, String context, String method) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext, method);
    }
}
