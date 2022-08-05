package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.ActionContext;

import java.text.MessageFormat;

public abstract class Struts2FieldValidatorSupport extends FieldValidatorSupport {
    protected void addError(String fieldName, Object object) {
        // get the pattern
        String objectMsg = getMessage(object);
        MessageFormat mf = new MessageFormat(objectMsg);

        // apply args
        String bundleValue = getValidatorContext().getText(getDefaultMessage());
        String msg = mf.format(new String[] {bundleValue});
        
        // add an error
        getValidatorContext().addFieldError(fieldName, msg);
    }

    protected void setFieldValue(String name, Object object, Object value) throws ValidationException {
        ValueStack stack = ActionContext.getContext().getValueStack();

        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        stack.setValue(name, value);

        if (pop) {
            stack.pop();
        }
    }

}
