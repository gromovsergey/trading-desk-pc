package com.foros.validation.constraint.validator;

import com.foros.model.EntityBase;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.EmailConstraint;
import com.foros.validation.constraint.ExpressionSymbolsOnlyConstraint;
import com.foros.validation.constraint.FractionDigitsConstraint;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.HtmlSymbolsOnlyConstraint;
import com.foros.validation.constraint.XmlAllowableConstraint;
import com.foros.validation.constraint.IdCollectionConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NoSecondsConstraint;
import com.foros.validation.constraint.NotEmptyConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SingleLineConstraint;
import com.foros.validation.constraint.SizeConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.UrlConstraint;
import com.foros.validation.constraint.ValuesConstraint;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class ValidatorFactories {

    public static class Required implements ValidatorFactory<RequiredConstraint, Object, RequiredValidator> {
        @Override
        public RequiredValidator validator(RequiredConstraint annotation) {
            return new RequiredValidator()
                    .withMessage(annotation.message());
        }
    }

    public static class StringSize implements ValidatorFactory<StringSizeConstraint, String, StringValidator> {
        @Override
        public StringValidator validator(StringSizeConstraint annotation) {
            return new StringValidator()
                    .withSize(annotation.size());
        }
    }

    public static class Values implements ValidatorFactory<ValuesConstraint, Object, ValuesValidator> {
        @Override
        public ValuesValidator validator(ValuesConstraint constraint) {
            return new ValuesValidator()
                    .withIgnoreCase(constraint.ignoreCase())
                    .withValues(constraint.values());
        }
    }

    public static class Url implements ValidatorFactory<UrlConstraint, String, UrlValidator> {
        @Override
        public UrlValidator validator(UrlConstraint annotation) {
            return new UrlValidator().withSchemas(annotation.schemas());
        }
    }

    public static class Range implements ValidatorFactory<RangeConstraint, Number, RangeValidator> {
        @Override
        public RangeValidator validator(RangeConstraint annotation) {
            return new RangeValidator()
                    .withMax(parse(annotation.max()))
                    .withMin(parse(annotation.min()));
        }

        private static BigDecimal parse(String str) {
            return str == null || str.isEmpty() ? null : new BigDecimal(str);
        }
    }

    public static class Pattern implements ValidatorFactory<PatternConstraint, String, PatternValidator> {
        @Override
        public PatternValidator validator(PatternConstraint annotation) {
            return new PatternValidator()
                    .withPattern(annotation.regexp())
                    .withMessage(annotation.message());
        }
    }

    public static class HtmlSymbolsOnly implements ValidatorFactory<HtmlSymbolsOnlyConstraint, String, PatternValidator> {

        private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^[^<>]*$");

        @Override
        public PatternValidator validator(HtmlSymbolsOnlyConstraint annotation) {
            return createValidator();
        }

        public static PatternValidator createValidator() {
            return new PatternValidator()
                    .withPattern(PATTERN)
                    .withMessage("errors.field.illegalSymbols")
                    .withParameters(" < > ");
        }
    }

    public static class ExpressionSymbolsOnly implements ValidatorFactory<ExpressionSymbolsOnlyConstraint, String, PatternValidator> {

        private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^[^<>\\[\\]\\|]*$");

        @Override
        public PatternValidator validator(ExpressionSymbolsOnlyConstraint annotation) {
            return createValidator();
        }

        public static PatternValidator createValidator() {
            return new PatternValidator()
                    .withPattern(PATTERN)
                    .withMessage("errors.field.illegalSymbols")
                    .withParameters("< > | [ ]");
        }
    }

    public static class SingleLine implements ValidatorFactory<SingleLineConstraint, String, PatternValidator> {

        private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("^[^\n\r]*$");

        @Override
        public PatternValidator validator(SingleLineConstraint annotation) {
            return new PatternValidator()
                    .withPattern(PATTERN)
                    .withMessage("errors.field.oneLine");
        }
    }

    public static class XmlAllowable implements ValidatorFactory<XmlAllowableConstraint, String, XmlAllowableValidator> {

        @Override
        public XmlAllowableValidator validator(XmlAllowableConstraint annotation) {
            return new XmlAllowableValidator();
        }
    }

    public static class ByteLength implements ValidatorFactory<ByteLengthConstraint, String, ByteLengthValidator> {

        @Override
        public ByteLengthValidator validator(ByteLengthConstraint annotation) {
            return new ByteLengthValidator()
                    .withLength(annotation.length());
        }
    }

    public static class Email implements ValidatorFactory<EmailConstraint, String, EmailValidator> {

        @Override
        public EmailValidator validator(EmailConstraint annotation) {
            return new EmailValidator();
        }
    }

    public static class OptionToken implements ValidatorFactory<Annotation, String, OptionTokenValidator> {

        @Override
        public OptionTokenValidator validator(Annotation annotation) {
            return new OptionTokenValidator();
        }
    }

    public static class NotNull implements ValidatorFactory<NotNullConstraint, Object, NotNullValidator> {

        @Override
        public NotNullValidator validator(NotNullConstraint annotation) {
            return new NotNullValidator()
                    .withModes(annotation.modes())
                    .excludeModes(annotation.excludedModes());
        }
    }

    public static class Id implements ValidatorFactory<IdConstraint, Object, IdValidator> {

        @Override
        public IdValidator validator(IdConstraint annotation) {
            return new IdValidator()
                    .withModes(annotation.modes())
                    .excludeModes(annotation.excludeModes());
        }
    }

    public static class Size implements ValidatorFactory<SizeConstraint, Object, SizeValidator> {

        @Override
        public SizeValidator validator(SizeConstraint annotation) {
            return new SizeValidator()
                    .withMax(annotation.max())
                    .withMin(annotation.min())
                    .withMessage(annotation.message());
        }
    }

    public static class FractionDigits implements ValidatorFactory<FractionDigitsConstraint, Number, FractionDigitsValidator> {

        @Override
        public FractionDigitsValidator validator(FractionDigitsConstraint annotation) {
            return new FractionDigitsValidator()
                    .withFraction(annotation.value());
        }
    }


    public static class HasId implements ValidatorFactory<HasIdConstraint, EntityBase, HasIdValidator> {

        @Override
        public HasIdValidator validator(HasIdConstraint annotation) {
            return new HasIdValidator()
                    .withModes(annotation.modes())
                    .excludeModes(annotation.excludedModes());
        }
    }

    public static class Name implements ValidatorFactory<NameConstraint, String, NameValidator> {

        @Override
        public NameValidator validator(NameConstraint annotation) {
            return new NameValidator();
        }
    }

    public static class NoSeconds implements ValidatorFactory<NoSecondsConstraint, Date, NoSecondsValidator> {

        @Override
        public NoSecondsValidator validator(NoSecondsConstraint annotation) {
            return new NoSecondsValidator();
        }
    }

    public static class IdCollection implements ValidatorFactory<IdCollectionConstraint, Iterable<Object>, IdCollectionValidator> {

        @Override
        public IdCollectionValidator validator(IdCollectionConstraint annotation) {
            return new IdCollectionValidator();
        }
    }

    public static class NotEmpty implements ValidatorFactory<NotEmptyConstraint, Collection<Object>, NotEmptyValidator> {

        @Override
        public NotEmptyValidator validator(NotEmptyConstraint annotation) {
            return new NotEmptyValidator()
                    .withMessage(annotation.message());
        }
    }

}
