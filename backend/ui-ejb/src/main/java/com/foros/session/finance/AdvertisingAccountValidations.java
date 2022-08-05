package com.foros.session.finance;

import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.currency.Currency;
import com.foros.model.security.BillingFrequency;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.account.AccountService;
import com.foros.session.admin.currency.CurrencyService;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.util.VATValidator;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

@LocalBean
@Stateless
@Validations
public class AdvertisingAccountValidations {
    @PersistenceContext(unitName = "AdServerPU")
    protected EntityManager em;

    @EJB
    private AccountService accountService;

    @EJB
    private AdvertisingFinanceService advertisingFinanceService;

    @EJB
    private CurrencyService currencyService;

    @Validation
    public void validateUpdateFinance(ValidationContext validationContext, @ValidateBean(ValidationMode.UPDATE) AdvertisingFinancialSettings financialSettings) {
        ValidationContext context = validationContext
                                        .subContext(financialSettings)
                                        .withMode(ValidationMode.UPDATE)
                                        .build();
        AdvertisingAccountBase account = (AdvertisingAccountBase) accountService.find(financialSettings.getAccountId());
        int maxFractionDigits = account.getCurrency().getFractionDigits();

        if (context.isReachable("minInvoice")) {
            context
                .validator(FractionDigitsValidator.class)
                .withFraction(maxFractionDigits)
                .withPath("minInvoice")
                .validate(financialSettings.getMinInvoice());
        }

        if (context.isReachable("creditLimit")) {
            context
                .validator(FractionDigitsValidator.class)
                .withFraction(maxFractionDigits)
                .withPath("creditLimit")
                .validate(financialSettings.getCreditLimit());

            if (context.props("creditLimit").noViolations()) {
                if (!advertisingFinanceService.isCreditLimitValid(account.getId(), financialSettings.getCreditLimit())) {
                    BigDecimal maxCreditLimit = advertisingFinanceService.getConvertedMaxCreditLimit(account.getId());
                    Locale locale = CurrentUserSettingsHolder.getLocale();
                    DecimalFormat df = NumberUtil.getCurrencyFormat(locale, account.getCurrency().getCurrencyCode());
                    String maxCreditLimitStr = df.format(maxCreditLimit);
                    context.addConstraintViolation("account.creditLimit.exceed")
                        .withParameters(maxCreditLimitStr)
                        .withPath("creditLimit")
                        .withValue(financialSettings.getCreditLimit());
                }
            }
        }

        ValidationContext dataContext = context.subContext(financialSettings.getData())
                .withPath("data")
                .build();

        if (dataContext.isReachable("prepaidAmount")) {
            BigDecimal prepaidAmount = financialSettings.getData().getPrepaidAmount();
            dataContext
                .validator(FractionDigitsValidator.class)
                .withFraction(maxFractionDigits)
                .withPath("prepaidAmount")
                .validate(prepaidAmount);

            dataContext
                .validator(RangeValidator.class)
                .withMin(BigDecimal.ZERO)
                .withMax(BigDecimal.valueOf(4999999999L))
                .withPath("prepaidAmount")
                .validate(prepaidAmount);
        }

        validateCommission(account, financialSettings, context);

        if (financialSettings.isChanged("mediaHandlingFee")) {
            if (financialSettings.getMediaHandlingFee() == null) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("mediaHandlingFee");
            }
        }

        if (financialSettings.isChanged("paymentTerms")) {
            String paymentTerms = financialSettings.getPaymentTerms();

            if (StringUtil.isPropertyNotEmpty(paymentTerms) && !StringUtil.isNumber(paymentTerms)) {
                context
                    .addConstraintViolation("errors.field.integer")
                    .withPath("paymentTerms");
            } else if (StringUtil.isNumber(paymentTerms)) {
                int intPaymentTerms = Integer.valueOf(paymentTerms);

                if (intPaymentTerms < 14 || intPaymentTerms > 60) {
                    context
                        .addConstraintViolation("errors.field.range")
                        .withPath("paymentTerms")
                        .withParameters(14, 60);
                }

            }
        }

        if (financialSettings.isChanged("billingFrequencyOffset")) {
            Long offset = financialSettings.getBillingFrequencyOffset();

            if (offset != null) {
                BillingFrequency billingFrequency = financialSettings.isChanged("billingFrequency") ? financialSettings.getBillingFrequency()
                        : account.getFinancialSettings().getBillingFrequency();

                if (billingFrequency != null && (billingFrequency.getMin() > offset || billingFrequency.getMax() < offset)) {
                    context
                            .addConstraintViolation("errors.field.range")
                            .withPath("billingFrequencyOffset")
                            .withParameters(billingFrequency.getMin(), billingFrequency.getMax());
                }
            }
        }

        if (financialSettings.isChanged("taxNumber")) {
            String taxNumber = financialSettings.getTaxNumber();

            if (StringUtil.isPropertyNotEmpty(taxNumber) && !VATValidator.isValid(account.getCountry().getCountryCode(), taxNumber)) {
                context
                    .addConstraintViolation("errors.finance.invalid.taxnumber")
                    .withPath("taxNumber");
            }
        }
    }

    @Validation
    public void validateUpdateCommission(ValidationContext validationContext, @ValidateBean(ValidationMode.UPDATE) AdvertisingFinancialSettings financialSettings) {
        ValidationContext context = validationContext
                .subContext(financialSettings)
                .withMode(ValidationMode.UPDATE)
                .build();
        validateCommission(financialSettings.getAccount(), financialSettings, context);
    }

    private void validateCommission(AdvertisingAccountBase account, AdvertisingFinancialSettings settings, ValidationContext context) {
        if (account instanceof AgencyAccount || !account.isStandalone()) {
            if (settings.isChanged("commission")) {
                if (settings.getCommission() == null) {
                    context
                            .addConstraintViolation("errors.field.required")
                            .withPath("commissionPercent");
                }
            }
        }
    }

    @Validation
    public void validatePrepaidAmount(ValidationContext validationContext, AdvertisingAccountBase account, ValidationMode validationMode) {
        ValidationContext context = validationContext
                .subContext(account.getFinancialSettings().getData())
                .withMode(validationMode)
                .withPath("financialSettings.data")
                .build();

        int maxFractionDigits;
        if (validationMode == ValidationMode.UPDATE) {
            AdvertisingAccountBase existingAccount = (AdvertisingAccountBase) accountService.find(account.getId());
            maxFractionDigits = existingAccount.getCurrency().getFractionDigits();
        } else {
            Currency currency = currencyService.findById(account.getCurrency().getId());
            maxFractionDigits = currency.getFractionDigits();
        }

        if (context.isReachable("prepaidAmount")) {
            BigDecimal prepaidAmount = account.getFinancialSettings().getData().getPrepaidAmount();
            if (prepaidAmount == null) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("prepaidAmount");
            }

            context
                    .validator(FractionDigitsValidator.class)
                    .withFraction(maxFractionDigits)
                    .withPath("prepaidAmount")
                    .validate(prepaidAmount);

            context
                    .validator(RangeValidator.class)
                    .withMin(BigDecimal.ZERO)
                    .withMax(BigDecimal.valueOf(4999999999L))
                    .withPath("prepaidAmount")
                    .validate(prepaidAmount);
        }
    }

}
