package com.foros.validation.constraint.validator;

public class OptionTokenValidator extends AbstractValidator<String, OptionTokenValidator> {

    @Override
    protected void validateValue(String value) {

        context().validator(PatternValidator.class)
                .withPath(path())
                .withPattern("^[\\w]*$")
                .withMessage("errors.field.onlyLatinOrDigitsOrUnderscore")
                .validate(value);

        context().validator(PatternValidator.class)
                .withPath(path())
                .withPattern("^([a-zA-Z]+.*)?$")
                .withMessage("errors.field.startWithLatin")
                .validate(value);

    }

}
