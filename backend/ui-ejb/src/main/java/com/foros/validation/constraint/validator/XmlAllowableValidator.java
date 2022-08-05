package com.foros.validation.constraint.validator;

import com.foros.util.StringUtil;

public class XmlAllowableValidator extends AbstractValidator<String, XmlAllowableValidator> {
    @Override
    protected void validateValue(String value) {
        if (!StringUtil.isPropertyEmpty(value)) {
            String invalid = StringUtil.ALLOWED_FOR_XML.matcher(value).replaceAll("");
            if (!invalid.isEmpty()) {
                addConstraintViolation("errors.field.notAllowedForXML").withValue(value);
            }
        }
    }
}
