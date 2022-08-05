package com.foros.framework.struts2validator;

import com.foros.util.KaptchaUtils;
import com.foros.util.StringUtil;

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.struts2.ServletActionContext;

public class CaptchaFieldValidator extends Struts2FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String value = (String) getFieldValue(fieldName, object);
        String captchaExpected = KaptchaUtils.read(ServletActionContext.getRequest().getSession());

        if (StringUtil.isPropertyEmpty(value) || !value.equalsIgnoreCase(captchaExpected)) {
            addError(fieldName, object);
        }
    }

}
