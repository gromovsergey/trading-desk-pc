package com.foros.session.admin.searchEngine;

import com.foros.model.admin.SearchEngine;
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
public class SearchEngineValidations {
    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) SearchEngine searchEngine) {
    }

    @Validation
    public void validateUpdate(
            ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) SearchEngine searchEngine) {
    }
}
