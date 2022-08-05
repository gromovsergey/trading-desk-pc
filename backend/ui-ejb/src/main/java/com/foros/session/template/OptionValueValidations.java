package com.foros.session.template;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionType;
import com.foros.model.template.OptionValue;
import com.foros.model.template.OptionValueUtils;
import com.foros.session.UrlValidations;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.FileSystem;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.util.CollectionUtils;
import com.foros.util.Function;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.preview.Substitution;
import com.foros.util.preview.SubstitutionTemplate;
import com.foros.util.preview.SubstitutionTemplate.TokenPrefix;
import com.foros.util.preview.SubstitutionTemplateParser;
import com.foros.util.url.URLValidator;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jparsec.error.ParserException;

@LocalBean
@Stateless
@Validations
public class OptionValueValidations {

    private static final long OPTION_VALUE_DEFAULT_MAX_LENGTH = 2000;
    private static final long OPTION_HTML_VALUE_DEFAULT_MAX_LENGTH = 20000;
    private static final String ONLY_COLOR_LETTERS = "^[1234567890ABCDEFabcdef]{6}+$";
    private static final Pattern COLOR_PATTERN = Pattern.compile(ONLY_COLOR_LETTERS);

    @EJB
    private OptionService optionService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private UrlValidations urlValidations;

    @Validation
    public void validateOptionValue(ValidationContext context, OptionValue optionValue, Account account) {
        Option option = optionService.findById(optionValue.getOption().getId());
        Long optionId = option.getId();

        String strOptionValue = optionValue.getValue();
        ValidationContext valueContext = context.createSubContext(optionValue.getValue(), "options[" + optionId + "].value");
        if (StringUtil.isPropertyEmpty(strOptionValue)) {
            if (option.isRequired()) {
                valueContext.addConstraintViolation("errors.field.required").withPath();
            }
            return;
        }

        validateValueLength(valueContext, option, optionValue.getValue());

        String substituted = validateSubstitutions(valueContext, option, optionValue.getValue());

        if (valueContext.hasViolations()) {
            return;
        }

        switch (option.getType()) {
        case INTEGER: {
            validateInteger(valueContext, option, substituted);
            break;
        }

        case COLOR: {
            validateColor(valueContext, substituted);
            break;
        }

        case ENUM: {
            validateEnum(valueContext, option, substituted);
            break;
        }

        case FILE:
        case DYNAMIC_FILE: {
            validateFile(valueContext, account, option, substituted);
            break;
        }

        case URL: {
            validateUrl(valueContext, substituted);
            break;
        }

        case URL_WITHOUT_PROTOCOL: {
            validateUrlWithoutProtocol(valueContext, substituted);
            break;
        }

        case FILE_URL: {
            validateFileUrl(valueContext, account, option, substituted);
            break;
        }

        case STRING:
        case TEXT:
        case HTML: {
            break;
        }

        default: {
            throw new IllegalArgumentException("Unknown option " + option.getType().getName());
        }
        }
    }

    @Validation
    public String validateSubstitutions(ValidationContext context, Option option, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        SubstitutionTemplate template;
        try {
            template = SubstitutionTemplateParser.parse(value);
        } catch (ParserException e) {
            context.addConstraintViolation("Option.errors.badFormat")
                    .withParameters(e.getLocation().line, e.getLocation().column)
                    .withValue(value);
            return null;
        }

        Set<String> allowedSubstitutionTokens = option.buildSubstitutionTokens();

        List<Substitution> substitutions = template.getSubstitutions();
        if (allowedSubstitutionTokens.isEmpty() && !substitutions.isEmpty()) {
            context.addConstraintViolation("Option.errors.substitutionsNotAllowed");
        } else {
            for (Substitution substitution : substitutions) {
                if (!allowedSubstitutionTokens.contains(substitution.getToken())) {
                    context.addConstraintViolation("Option.errors.invalidSubstitutionToken")
                        .withParameters(substitution.getToken(), allowedSubstitutionTokens);
                }

                if (substitution.getPrefix() != null) {
                    TokenPrefix prefix = TokenPrefix.byNameOptional(substitution.getPrefix());
                    if(prefix == null) {
                        context.addConstraintViolation("Option.errors.invalidSubstitutionPrefix")
                                .withParameters(substitution.getPrefix(), TokenPrefix.names())
                                .withValue(value);
                    }
                }
            }
        }

        if (context.hasViolations()) {
            return null;
        }

        return template.render(new Function<String, String>() {
            @Override
            public String apply(String token) {
                return "";
            }
        });
    }

    public void validateValueLength(ValidationContext context, Option option, String value) {
        if (value == null) {
            return;
        }

        Long maxLength = option.getMaxLength();
        Long maxLengthFullWidth = option.getMaxLengthFullWidth();

        if (maxLength == null && maxLengthFullWidth == null) {
            maxLength = OptionType.HTML == option.getType() ? OPTION_HTML_VALUE_DEFAULT_MAX_LENGTH : OPTION_VALUE_DEFAULT_MAX_LENGTH;
        }
        if (maxLength != null && maxLength < value.length()) {
            context.addConstraintViolation("errors.field.maxlength")
                    .withParameters(maxLength)
                    .withValue(value);
        }

        if (maxLengthFullWidth != null && maxLengthFullWidth < StringUtil.countCharsCJK(value)) {
            context.addConstraintViolation("errors.field.invalidMaxLengthExc")
                    .withParameters(maxLengthFullWidth)
                    .withValue(value);
        }
    }

    private void validateFileUrl(ValidationContext context, Account account, Option option, String value) {
        if (StringUtil.startsWith(value, "http://", "https://", "//")) {
            validateUrl(context, URLValidator.urlForValidate(value));
        } else {
            validateFile(context, account, option, value);
        }
    }

    private void validateInteger(ValidationContext context, Option option, String strOptionValue) {
        if (!NumberUtil.isLong(strOptionValue)) {
            context.addConstraintViolation("errors.field.integer").withValue(strOptionValue);
            return;
        }

        Long parsed = NumberUtil.parseLong(strOptionValue);
        Long minValue = option.getMinValue() != null ? option.getMinValue() : -9999999999L;
        if (minValue != null && minValue > parsed) {
            context.addConstraintViolation("errors.field.less").withParameters(minValue).withValue(strOptionValue);
            return;
        }

        Long maxValue = option.getMaxValue() != null ? option.getMaxValue() : 9999999999L;
        if (maxValue != null && maxValue < parsed) {
            context.addConstraintViolation("errors.field.notgreater").withParameters(maxValue).withValue(strOptionValue);
        }
    }

    private void validateColor(ValidationContext context, String value) {
        if (!COLOR_PATTERN.matcher(value).matches()) {
            context.addConstraintViolation("Option.error.invalidColor").withValue(value);
        }
    }

    private void validateEnum(ValidationContext context, Option option, String value) {
        Set<String> optionValues = new HashSet<>(option.getValues().size());

        for (OptionEnumValue enumValue : option.getValues()) {
            optionValues.add(enumValue.getValue());
        }

        if (!optionValues.contains(value)) {
            context.addConstraintViolation("Option.error.invalidEnumValue")
                    .withParameters(CollectionUtils.toString(true, optionValues))
                    .withValue(value);
        }
    }

    private void validateUrl(ValidationContext context, String value) {
        urlValidations.validateUrl(context, URLValidator.urlForValidate(value), "", true);
    }

    private void validateUrlWithoutProtocol(ValidationContext context, String value) {
        urlValidations.validateUrl(context, value, "", UrlValidations.NO_SCHEMA, true);
    }

    private void validateFile(ValidationContext context, Account account, Option option, String value) {
        try {
            FileSystem fileSystem;
            if (account instanceof AdvertiserAccount) {
                fileSystem = getCreativeFS((AdvertiserAccount) account);
            } else if (account instanceof PublisherAccount) {
                fileSystem = getPublisherFS((PublisherAccount) account);
            } else {
                throw new RuntimeException("Account is invalid");
            }

            if (!fileSystem.checkExist(value)) {
                value = escapeFileUrl(value);
                context.addConstraintViolation("errors.fileexist")
                        .withParameters(value)
                        .withValue(value);
                return;
            }

            if (!FileUtils.isFileAllowed(value, option.getFileTypes())) {
                value = escapeFileUrl(FileUtils.getExtension(value));
                context.addConstraintViolation("errors.fileTypeIsNotAllowed")
                        .withParameters(value)
                        .withValue(value).withParameters(value);
           }
        } catch (BadNameException e) {
            context.addConstraintViolation("errors.fileexist").withValue(value).withParameters(value);
        }
    }

    private String escapeFileUrl(String value) {
        return StringEscapeUtils.escapeHtml(value).replaceAll("[\']", "&#39;");
    }

    private FileSystem getCreativeFS(AdvertiserAccount account) {
        String accountFolderName = OptionValueUtils.getAdvertiserRoot(account);
        PathProvider pathProvider = pathProviderService.getCreatives().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return pathProviderService.createFileSystem(pathProvider);
    }

    private FileSystem getPublisherFS(PublisherAccount account) {
        String accountFolderName = OptionValueUtils.getPublisherRoot(account);
        PathProvider pathProvider = pathProviderService.getPublisherAccounts().getNested(accountFolderName, OnNoProviderRoot.AutoCreate);
        return pathProviderService.createFileSystem(pathProvider);
    }

}
