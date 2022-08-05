package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;

public class RequiredFieldValidator  extends Struts2FieldValidatorSupport {
    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (value == null) {
            addError(fieldName, object);
        }
    }
}
