package com.foros.session.account;

import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class AccountInAgencySelectorValidations {

    @Validation
    public void validateAccount(ValidationContext context, @ValidateBean AccountInAgencySelector selector) {
    }

}
