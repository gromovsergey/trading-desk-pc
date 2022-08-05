package com.foros.framework.struts2validator;

import java.io.File;
import java.text.MessageFormat;

import com.opensymphony.xwork2.validator.ValidationException;

public class FileLengthValidator extends Struts2FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        File file = (File) getFieldValue(fieldName, object);

        if (file != null && (!file.exists() || file.length() == 0)) {
            addError(fieldName, object);
        }
    }

    protected void addError(String fieldName, Object object) {
        // get the pattern
        String objectMsg = getMessage(object);
        MessageFormat mf = new MessageFormat(objectMsg);

        // get filename 
        String bundleValue = "";
        try {
            bundleValue = (String)getFieldValue(fieldName + "FileName", object);
        } catch (ValidationException e) {            
            log.warn("Can't find field with name : " + fieldName + "FileName", e);
        }
        String msg = mf.format(new String[] {bundleValue});

        // add an error
        getValidatorContext().addFieldError(fieldName, msg);
    }
}
