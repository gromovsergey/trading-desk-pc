package com.foros.framework.struts2validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;
import java.util.List;
import java.util.Map;

public class ConversionErrorsTransformator extends ValidatorSupport {

    private String fieldMask;

    @Override
    public void validate(Object object) throws ValidationException {
        ActionContext context = ActionContext.getContext();
        Map<String, Object> conversionErrors = context.getConversionErrors();

        for (String fieldName : conversionErrors.keySet()) {
            if (fieldName.matches(fieldMask)) {

                Map<String, List<String>> errors = getValidatorContext().getFieldErrors();
                errors.remove(fieldName);
                getValidatorContext().setFieldErrors(errors);

                addFieldError(fieldName, object);
            }
        }
    }

    public void setFieldMask(String fieldMask) {
        this.fieldMask = fieldMask;
    }
}
