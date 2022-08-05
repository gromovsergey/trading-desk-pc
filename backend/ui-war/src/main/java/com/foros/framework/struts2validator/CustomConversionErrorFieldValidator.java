package com.foros.framework.struts2validator;

import com.foros.action.ConstraintValidationsAware;
import com.foros.validation.code.InputErrors;
import com.foros.validation.constraint.violation.ConstraintViolationImpl;
import com.foros.validation.constraint.violation.Path;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class CustomConversionErrorFieldValidator extends FieldValidatorSupport {

    private String fieldKey;

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        ActionContext context = ActionContext.getContext();
        Map conversionErrors = context.getConversionErrors();

        if (conversionErrors.containsKey(fullFieldName)) {
            Map<String, List<String>> errors = getValidatorContext().getFieldErrors();
            errors.remove(fieldName);
            getValidatorContext().setFieldErrors(errors);

            addError(fieldName, object);
        }
    }

    private void addError(String fieldName, Object object) {
        ValidatorContext validatorContext = getValidatorContext();

        // get the pattern
        String objectMsg = getMessage(object);
        MessageFormat mf = new MessageFormat(objectMsg);

        // apply args

        String bundleValue = validatorContext.getText(fieldKey);
        String msg = mf.format(new String[] {bundleValue});

        // add an error
        validatorContext.addFieldError(fieldName, msg);

        if (validatorContext instanceof ConstraintValidationsAware) {
            ConstraintValidationsAware cva = (ConstraintValidationsAware) validatorContext;
            cva.getConstraintViolations().add(new ConstraintViolationImpl(
                    InputErrors.UI_PARSE_ERROR,
                    msg,
                    Path.fromString(fieldName),
                    null,
                    messageKey
            ));
        }
    }
}
