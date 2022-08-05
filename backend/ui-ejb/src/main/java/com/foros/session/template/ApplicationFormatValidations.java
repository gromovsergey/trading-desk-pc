package com.foros.session.template;

import com.foros.model.template.ApplicationFormat;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import com.foros.validation.strategy.ValidationMode;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class ApplicationFormatValidations {
    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) ApplicationFormat applicationFormat) {
    }

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) ApplicationFormat applicationFormat) {
    }
}
