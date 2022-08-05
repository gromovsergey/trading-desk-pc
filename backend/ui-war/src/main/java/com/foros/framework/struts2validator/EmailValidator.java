package com.foros.framework.struts2validator;

import com.foros.util.ValidatorEx;

import com.opensymphony.xwork2.validator.ValidationException;

public class EmailValidator extends Struts2FieldValidatorSupport {
    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String value = (String) getFieldValue(fieldName, object);

        if (value == null || value.trim().length()==0) {
            return;
        }

        boolean isValid = ValidatorEx.validateEmail(value);
        if (!isValid) {
            addError(fieldName, object);
        }
    }
}
