package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.ValidationException;

/**
 * Since OUI has separate trimming interceptor this validator never do trimming.
 */
public class StringLengthFieldValidator extends Struts2FieldValidatorSupport {

    private int maxLength = -1;
    private int minLength = -1;


    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMinLength() {
        return minLength;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String val = (String) getFieldValue(fieldName, object);

        if (val == null || val.length() <= 0) {
            // use a required validator for these
            return;
        }

        if ((minLength > -1) && (val.length() < minLength)) {
        	addError(fieldName, object);
        } else if ((maxLength > -1) && (val.length() > maxLength)) {
        	addError(fieldName, object);
        }
    }
    
    // need for @StringLengthFieldValidator
    public void setTrim(boolean trim) {} 
}
