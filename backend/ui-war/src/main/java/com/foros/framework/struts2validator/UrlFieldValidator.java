package com.foros.framework.struts2validator;

import com.foros.util.url.URLValidator;

import com.opensymphony.xwork2.validator.ValidationException;

public class UrlFieldValidator extends Struts2FieldValidatorSupport {

    @Override
    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String url = (String) getFieldValue(getFieldName(), object);

        if (!URLValidator.isValid(url)) {
        	addError(fieldName, object);
        }
    }

}
