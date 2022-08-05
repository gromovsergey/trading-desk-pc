package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;
import com.foros.util.url.URLValidator;

public class UrlValidator extends AbstractValidator<String, UrlValidator> {
    private String[] schemas;

    @Override
    protected void validateValue(String value) {
        if (!StringUtil.isPropertyEmpty(value) && !URLValidator.isValid(URLValidator.urlForValidate(value, schemas))) {
            addConstraintViolation("errors.field.invalidUrl")
                    .withValue(value);
        }
    }

    public UrlValidator withSchemas(String[] schemas) {
        this.schemas = schemas;
        return this;
    }
}
