package com.foros.session.colocation;

import com.foros.model.isp.Colocation;
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
public class ColocationValidations {
    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) Colocation colocation) {
    }

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) Colocation colocation) {
    }
}
