package com.foros.validation.constraint.validator;

public class NameValidator extends AbstractValidator<String, NameValidator> {

    @Override
    protected void validateValue(String value) {
        context().validator(StringValidator.class)
                .withSize(100)
                .withPath(path())
                .validate(value);

        context().validator(ValidatorFactories.HtmlSymbolsOnly.createValidator())
                .withPath(path())
                .validate(value);
    }

}
