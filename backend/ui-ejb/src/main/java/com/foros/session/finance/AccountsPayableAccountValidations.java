package com.foros.session.finance;

import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.PublisherAccount;
import com.foros.session.account.AccountService;
import com.foros.util.IBANValidator;
import com.foros.util.StringUtil;
import com.foros.util.VATValidator;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class AccountsPayableAccountValidations {
    private static final Pattern UK_ACCOUNT_NUMBER_PATTERN = Pattern.compile("^([0-9]{8})$");
    private static final Pattern UK_SORT_CODE_PATTERN = Pattern.compile("^([0-9]{6})$");
    private static final Pattern BIC_PATTERN = Pattern.compile("^([a-zA-Z]{4})([a-zA-Z]{2})([a-zA-Z0-9]{2})([a-zA-Z0-9]{3})?$");

    @EJB
    private AccountService accountService;

    @Validation
    public void validateUpdateFinance(ValidationContext validationContext, @ValidateBean(ValidationMode.UPDATE) AccountsPayableFinancialSettings financialSettings) {
        ValidationContext context = validationContext
                .subContext(financialSettings)
                .withMode(ValidationMode.UPDATE)
                .build();

        AccountsPayableAccountBase account = (AccountsPayableAccountBase) accountService.find(financialSettings.getAccountId());

        if ("GB".equals(financialSettings.getBankCountry().getCountryCode())) {
            validateUKBankAccountNumber(context, financialSettings);
            validateSortCode(context, financialSettings);
        } else {
            validateBIC(context, financialSettings);
            validateIBAN(context, financialSettings);
        }

        if (account instanceof PublisherAccount) {
            if (context.isReachable("commission") && financialSettings.getCommission() == null) {
                context
                    .addConstraintViolation("errors.field.required")
                    .withPath("commissionPercent");
            }
        }

        if (context.isReachable("taxNumber")) {
            String taxNumber = financialSettings.getTaxNumber();

            if (StringUtil.isPropertyNotEmpty(taxNumber)) {
                if (!VATValidator.isValid(account.getCountry().getCountryCode(), taxNumber)) {
                    context
                            .addConstraintViolation("errors.finance.invalid.taxnumber")
                            .withPath("taxNumber");
                }
            }
        }
    }

    private void validateUKBankAccountNumber(ValidationContext context, AccountsPayableFinancialSettings financialSettings) {
        if (context.isReachable("bankAccountNumber")) {
            if (!StringUtil.isPropertyEmpty(financialSettings.getBankAccountNumber())) {
                Matcher matcher = UK_ACCOUNT_NUMBER_PATTERN.matcher(financialSettings.getBankAccountNumber());

                if (!matcher.matches()) {
                    context.addConstraintViolation("errors.finance.invalid.account.number")
                            .withPath("bankAccountNumber");
                }
            }
        }
    }

    private void validateSortCode(ValidationContext context, AccountsPayableFinancialSettings financialSettings) {
        if (context.isReachable("bankSortCode")) {
            if (StringUtil.isPropertyEmpty(financialSettings.getBankSortCode())) {
                context.addConstraintViolation("errors.field.required")
                    .withPath("bankSortCode");
            } else {
                Matcher matcher = UK_SORT_CODE_PATTERN.matcher(financialSettings.getBankSortCode());

                if (!matcher.matches()) {
                    context.addConstraintViolation("errors.finance.invalid.sort.code")
                        .withPath("bankSortCode");
                }
            }
        }
    }

    private void validateBIC(ValidationContext context, AccountsPayableFinancialSettings financialSettings) {
        if (context.isReachable("bankBicCode")) {
            if (StringUtil.isPropertyEmpty(financialSettings.getBankBicCode())) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("bankBicCode");
            } else {
                Matcher matcher = BIC_PATTERN.matcher(financialSettings.getBankBicCode());

                if (!matcher.matches()) {
                    context.addConstraintViolation("errors.finance.invalid.bic")
                            .withPath("bankBicCode");
                }
            }
        }
    }

    private void validateIBAN(ValidationContext context, AccountsPayableFinancialSettings financialSettings) {
        if (context.isReachable("bankAccountIban") && !StringUtil.isPropertyEmpty(financialSettings.getBankAccountIban())) {
            if (!IBANValidator.isCheckDigitValid(financialSettings.getBankAccountIban())) {
                context.addConstraintViolation("errors.finance.invalid.iban")
                    .withPath("bankAccountIban");
            }
        }
    }
}
