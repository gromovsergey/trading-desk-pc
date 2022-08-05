package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.ValidationException;

public class RequiredStringValidator extends Struts2FieldValidatorSupport {
    private boolean doTrim = true;

    public void setTrim(boolean trim) {
        doTrim = trim;
    }

    public boolean getTrim() {
        return doTrim;
    }

    @Override
    protected Object getFieldValue(String name, Object object) throws ValidationException {
        ActionContext context = ActionContext.getContext();
        Object value = null;
        Object fieldValues = context.getParameters().get(name);
        if (fieldValues != null) {
            if (fieldValues instanceof String) {
                value = fieldValues;
            } else if (fieldValues instanceof String[]) {
                String[] fieldValuesArray = (String[]) fieldValues;
                if (fieldValuesArray.length > 0) {
                    value = fieldValuesArray[0];
                }
            }
        }
        return value;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (!(value instanceof String)) {
            addError(fieldName, object);
        } else {
            String s = (String) value;

            if (doTrim) {
                s = s.trim();
            }

            if (s.length() == 0) {
                addError(fieldName, object);
            }
        }
    }
}
