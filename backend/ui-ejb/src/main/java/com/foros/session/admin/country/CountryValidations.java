package com.foros.session.admin.country;

import com.foros.model.AddressField;
import com.foros.model.Country;
import com.foros.model.IdNameEntity;
import com.foros.model.LocalizableName;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import com.foros.validation.strategy.ValidationMode;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.apache.commons.lang.StringUtils;

@LocalBean
@Stateless
@Validations
public class CountryValidations {
    private static final String ONLY_HTML_SYMBOLS = "^[^<>&]*$";
    private static final int MAX_ADDRESS_FIELD_LENGTH = 100;

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Country country) {
        validateThreshold(context, country);
        // validate address fields
        validateAddressFields(context, country);
        validateCategories(context, country);

        if (country.isVatEnabled()) {
            if (country.getDefaultVATRateView() == null && context.isReachable("defaultVATRateView")) {
                context.addConstraintViolation("errors.field.required").withPath("defaultVATRateView");
            }
        }
    }

    private void validateThreshold(ValidationContext context, Country country) {
        if (country.getHighChannelThreshold() == null || country.getLowChannelThreshold() == null) {
            return;
        }
        boolean isNonZeroEqual = country.getHighChannelThreshold().longValue() == country.getLowChannelThreshold().longValue() && country.getLowChannelThreshold() != 0L;
        boolean isLowMoreHigh = country.getHighChannelThreshold() < country.getLowChannelThreshold();
        if (isNonZeroEqual || isLowMoreHigh) {
            context.addConstraintViolation("Country.channelThreshold.highGreaterLow")
                    .withPath("highChannelThreshold");
        }
    }

    private void validateAddressFields(ValidationContext context, Country country) {
        int i = 0;
        for (AddressField af : country.getAddressFields()) {
            if (!CountryService.PredefinedAddressField.COUNTRY.getName().equals(af.getOFFieldName())) {
                LocalizableName name = af.getName();
                String defaultName = name.getDefaultName();
                String resourceKey = name.getResourceKey();

                if (resourceKey != null) {
                    if (StringUtil.trimmedUTF8length(resourceKey) > MAX_ADDRESS_FIELD_LENGTH) {
                        context.addConstraintViolation("errors.field.invalidMaxLengthExc")
                        .withPath("addressFieldsList[" + i + "].name.resourceKey");
                    }

                    if (!resourceKey.matches(ONLY_HTML_SYMBOLS)) {
                        context.addConstraintViolation("errors.field.invalid")
                        .withPath("addressFieldsList[" + i + "].name.resourceKey");
                    }
                }

                if (StringUtils.isBlank(defaultName)) {
                    context.addConstraintViolation("errors.field.required")
                    .withPath("addressFieldsList[" + i + "].name.defaultName");
                } else if (StringUtil.trimmedUTF8length(defaultName) > MAX_ADDRESS_FIELD_LENGTH) {
                    context.addConstraintViolation("errors.field.invalidMaxLengthExc")
                    .withPath("addressFieldsList[" + i + "].name.defaultName");
                } else if (!defaultName.matches(ONLY_HTML_SYMBOLS)) {
                    context.addConstraintViolation("errors.field.invalid")
                    .withPath("addressFieldsList[" + i + "].name.defaultName");
                }
            }
            i++;
        }
    }

    private void validateCategories(ValidationContext context, Country country) {
        validateCategories(context, country.getContentCategories(), "contentCategory");
        validateCategories(context, country.getSiteCategories(), "siteCategory");
    }

    private void validateCategories(ValidationContext context, Set<? extends IdNameEntity> categories, String name) {
        Set<String> categoryNames = new HashSet<String>();
        int errorIndex = 0;
        for (IdNameEntity category : categories) {
            if (StringUtils.isBlank(category.getName())) {
                // name should not be empty, add error message
                context.addConstraintViolation("errors.field.required").withPath(name + "[" + errorIndex + "].name");
            } else if (category.getName().length() > 200) {
                context.addConstraintViolation("errors.field.maxlength")
                        .withParameters(200)
                        .withPath(name + "[" + errorIndex + "].name");
            } else {
                // duplicate category check to show user friendly message on UI
                if (!categoryNames.add(category.getName())) {
                    context.addConstraintViolation("errors.duplicate")
                            .withPath(name + "[" + errorIndex + "].name")
                            .withParameters("{Country.category.name}");
                }
            }
            errorIndex++;
        }
    }
}
