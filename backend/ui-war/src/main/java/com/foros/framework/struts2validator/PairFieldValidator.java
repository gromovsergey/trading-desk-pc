package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.foros.util.PairUtil;

public class PairFieldValidator extends Struts2FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String value = (String) getFieldValue(fieldName, object);

        if (value != null && !"".equals(value) && !PairUtil.validatePair(value)) {
            addError(fieldName, object);
        }
    }

}