package com.foros.session.account;

import com.foros.model.Country;
import com.foros.model.account.*;
import com.foros.model.security.AccountType;
import com.foros.model.security.TextAdservingMode;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.session.BeanValidations;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.finance.AdvertisingAccountValidations;
import com.foros.util.CNPJValidator;
import com.foros.util.FlagsUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.constraint.validator.RequiredValidator;
import com.foros.validation.strategy.ValidationMode;
import org.apache.commons.lang.ObjectUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@LocalBean
@Stateless
@Validations
public class AccountValidations {

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private AccountService accountService;

    @EJB
    private AccountTypeService accountTypeService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private AdvertisingAccountValidations advertisingAccountValidations;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Validation
    public void validateCreateInternalAccount(ValidationContext context, @ValidateBean(ValidationMode.CREATE) InternalAccount account) {
        validateInternalTestFlag(account);

        beanValidations
                .linkValidator(context, Country.class)
                .withPath("country")
                .validate(account.getCountry());

        if (!context.hasViolations()) {
            if (!CNPJValidator.isValid(account.getCountry().getCountryCode(), account.getCompanyRegistrationNumber())) {
                context
                        .addConstraintViolation("errors.invalidcnpj")
                        .withPath("companyRegistrationNumber");
            }
        }
    }

    @Validation
    public void validateCreateExternalAccount(ValidationContext context, @ValidateBean(ValidationMode.CREATE) ExternalAccount account) {
        if (account.isChanged("flags")) {
            boolean isTestFlagSet = FlagsUtil.get(account.getFlags(), Account.TEST_FLAG);

            if (isTestFlagSet && !accountRestrictions.canSetTestFlag(account)) {
                context
                        .addConstraintViolation("account.flagChanged.error")
                        .withPath("testFlag");
            }
        }

        beanValidations
                .linkValidator(context, Country.class)
                .withPath("country")
                .validate(account.getCountry());

        if (!context.hasViolations()) {
            if (!CNPJValidator.isValid(account.getCountry().getCountryCode(), account.getCompanyRegistrationNumber())) {
                context
                        .addConstraintViolation("errors.invalidcnpj")
                        .withPath("companyRegistrationNumber");
            }
        }
    }

    @Validation
    public void validateCreateAgencyAccount(ValidationContext context, @ValidateBean(ValidationMode.CREATE) AgencyAccount account) {
        validateCreateExternalAccount(context, account);
        validateAccountCommission(context, account);
        advertisingAccountValidations.validatePrepaidAmount(context, account, ValidationMode.CREATE);
    }

    @Validation
    public void validateCreateStandaloneAdvertiserAccount(ValidationContext context, @ValidateBean(ValidationMode.CREATE) AdvertiserAccount account) {
        validateCreateExternalAccount(context, account);
        advertisingAccountValidations.validatePrepaidAmount(context, account, ValidationMode.CREATE);
    }

    @Validation
    public void validateUpdateInternalAccount(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) InternalAccount account) {
        InternalAccount existingAccount = accountService.findInternalAccount(account.getId());
        if (existingAccount == null) {
            context
                .addConstraintViolation("errors.account.internal.notFound")
                .withParameters(account.getId())
                .withPath("id")
                .withValue(account.getId());
            return;
        }

        validateInternalTestFlag(account);

        if (!CNPJValidator.isValid(existingAccount.getCountry().getCountryCode(), account.getCompanyRegistrationNumber())) {
            context
                .addConstraintViolation("errors.invalidcnpj")
                .withPath("companyRegistrationNumber");
        }
    }

    @Validation
    public void validateUpdateExternalAccount(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) ExternalAccount account) {
        ExternalAccount existingAccount = (ExternalAccount) accountService.find(account.getId());
        if (existingAccount == null) {
            context
                .addConstraintViolation("errors.account.external.notFound")
                .withParameters(account.getId())
                .withPath("id")
                .withValue(account.getId());
            return;
        }

        if (account.isChanged("flags")) {
            validateCheckFlags(context, existingAccount, account.getFlags());
        }

        if (!CNPJValidator.isValid(existingAccount.getCountry().getCountryCode(), account.getCompanyRegistrationNumber())) {
            context
                .addConstraintViolation("errors.invalidcnpj")
                .withPath("companyRegistrationNumber");
        }

        if (account instanceof AdvertisingAccountBase && account.isSelfServiceFlag() ){
            context.validator(RequiredValidator.class)
                    .withPath("selfServiceCommissionPercent")
                    .withMessage("errors.field.required")
                    .validate(account.getSelfServiceCommissionPercent());

            context.validator(FractionDigitsValidator.class)
                    .withPath("selfServiceCommissionPercent")
                    .withFraction(2)
                    .validate(account.getSelfServiceCommissionPercent());

            context.validator(RangeValidator.class)
                    .withMin(BigDecimal.ZERO)
                    .withMax(new BigDecimal("100"), 2)
                    .withPath("selfServiceCommissionPercent")
                    .validate(account.getSelfServiceCommissionPercent());
        }
    }

    @Validation
    public void validateUpdateAgencyAccount(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AgencyAccount account) {
        validateUpdateExternalAccount(context, account);
        validateAccountCommission(context, account);
        advertisingAccountValidations.validatePrepaidAmount(context, account, ValidationMode.UPDATE);
    }

    @Validation
    public void validateUpdateStandaloneAdvertiserAccount(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AdvertiserAccount account) {
        validateUpdateExternalAccount(context, account);
        advertisingAccountValidations.validatePrepaidAmount(context, account, ValidationMode.UPDATE);
    }

    @Validation
    public void validateInternalAccountDependencies(ValidationContext context, InternalAccount account) {
        validateCommonAccountDependencies(context, account);
    }

    @Validation
    public void validateExternalAccountDependencies(ValidationContext context, ExternalAccount account) {
        validateCommonAccountDependencies(context, account);

        if (account.isChanged("internalAccount") && account.getInternalAccount() == null) {
            throw new RuntimeException("Invalid internal account");
        }

        if (account.isChanged("accountManager") && account.getAccountManager() != null) {
            InternalAccount internalAccount;
            User accountManager = account.getAccountManager();

            if (account.getId() == null) {
                internalAccount = account.getInternalAccount();
            } else {
                internalAccount = ((ExternalAccount) accountService.find(account.getId())).getInternalAccount();
            }

            if (!accountManager.getRole().isManagerOf(account.getRole()) || !accountManager.getAccount().equals(internalAccount)) {
                throw new RuntimeException("Invalid account manager.");
            }
        }
    }

    private void validateCommonAccountDependencies(ValidationContext context, Account account) {
        if (account.isChanged("country") && account.getCountry() == null) {
            throw new RuntimeException("Invalid country");
        }

        if (account.isChanged("currency") && account.getCurrency() == null) {
            throw new RuntimeException("Invalid currency");
        }

        if (account.isChanged("timezone") && account.getTimezone() == null) {
            throw new RuntimeException("Invalid timezone");
        }

        if (account.isChanged("accountType") && account.getAccountType() == null) {
            throw new RuntimeException("Invalid account type");
        }
    }

    private void validateInternalTestFlag(InternalAccount account) {
        if (account.getTestFlag()) {
            throw new RuntimeException("Test flag is not permitted for internal account.");
        }
    }

    @Validation
    public void validateTextAdservingMode(ValidationContext context, ExternalAccount account) {
        if (account instanceof AdvertisingAccountBase) {
            AdvertisingAccountBase advAccount = (AdvertisingAccountBase) account;
            if (AccountRole.ADVERTISER == advAccount.getRole()
                && advAccount.getTextAdservingMode() == TextAdservingMode.ONE_TEXT_AD_PER_ADVERTISER) {
                throw new RuntimeException("Account has wrong TextAdservingMode");
            }
        }
    }

    @Validation
    public void validateRole(ValidationContext context, Account account) {
        if (account.isChanged("accountType") && account.getRole() != account.getAccountType().getAccountRole()) {
            throw new RuntimeException("Not permitted account type.");
        }
    }

    @Validation
    public void validateUpdateContacts(ValidationContext context, InternalAccount existingAccount, InternalAccount account) {
        if (account.isChanged("advContact") && account.getAdvContact() != null) {
            checkContact(existingAccount, account.getAdvContact());
        }

        if (account.isChanged("pubContact") && account.getPubContact() != null) {
            checkContact(existingAccount, account.getPubContact());
        }

        if (account.isChanged("ispContact") && account.getIspContact() != null) {
            checkContact(existingAccount, account.getIspContact());
        }

        if (account.isChanged("cmpContact") && account.getCmpContact() != null) {
            checkContact(existingAccount, account.getCmpContact());
        }
    }

    private void checkContact(InternalAccount account, User contact) {
        if (contact != null && !account.getUsers().contains(contact)) {
            throw new RuntimeException("Not permitted contact.");
        }
    }

    private void validateCheckFlags(ValidationContext context, Account existingAccount, Long flags) {
        // Do not allow clear flags
        long oldFlags = existingAccount.getFlags();

        if (flags == null) {
            flags = 0L;
        }

        boolean isTestFlagChanged = (flags & Account.TEST_FLAG) != (oldFlags & Account.TEST_FLAG);

        if (isTestFlagChanged && !accountRestrictions.canSetTestFlag(existingAccount)) {
            context
                .addConstraintViolation("account.flagChanged.error")
                .withPath("testFlag");
        }

        // international flag was set earlier and now it is cleared
        if (((flags & Account.INTERNATIONAL) == 0) && (oldFlags & Account.INTERNATIONAL) != 0) {
            context
                .addConstraintViolation("account.flagDisabled.error")
                .withPath("internationalFlag");
        }
    }

    @Validation
    public void validateCheckAccountType(ValidationContext validationContext, Account existingAccount, Account account) {
        ValidationContext context = validationContext.createSubContext(account);

        if (account.isChanged("accountType")) {
            AccountType accountType = existingAccount.getAccountType();
            if (!ObjectUtils.equals(accountType.getId(), account.getAccountType().getId())) {
                AccountType oldAccountType = existingAccount.getAccountType();
                AccountType newAccountType = account.getAccountType();

                if (!accountTypeService.checkAccountCanMoved(account, oldAccountType, newAccountType)) {
                    context
                        .addConstraintViolation("account.illegalAccountType")
                        .withPath("accountType");
                }
            }
        }
    }

    @Validation
    public void validateAddAdvertiser(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AdvertiserAccount advertiser) {
        if (advertiser.getAgency() == null || advertiser.getAgency().getId() == null) {
            throw new RuntimeException("Agency must be defined");
        }
        validateAccountCommission(context, advertiser);
        advertisingAccountValidations.validatePrepaidAmount(context, advertiser, ValidationMode.CREATE);
    }

    @Validation
    public void validateUpdateAdvertiser(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AdvertiserAccount advertiser) {
        AdvertiserAccount existingAdvertiser = accountService.findAdvertiserAccount(advertiser.getId());
        if (existingAdvertiser == null) {
            context
                    .addConstraintViolation("errors.account.advertiser.notFound")
                    .withParameters(advertiser.getId())
                    .withPath("id")
                    .withValue(advertiser.getId());
            return;
        }
        validateAccountCommission(context, advertiser);
        advertisingAccountValidations.validatePrepaidAmount(context, advertiser, ValidationMode.UPDATE);
    }

    private void validateAccountCommission(ValidationContext context, AdvertisingAccountBase account) {
        beanValidations.validateBean(context, "", account.getFinancialSettings(), ValidationMode.UPDATE);
        advertisingAccountValidations.validateUpdateCommission(context, account.getFinancialSettings());
    }
}
