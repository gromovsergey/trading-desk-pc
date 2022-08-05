package com.foros.session.finance;

import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.Country;
import com.foros.model.account.Account;
import com.foros.model.account.AccountsPayableAccountBase;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.currency.Currency;
import com.foros.model.security.ActionType;
import com.foros.model.security.PaymentMethod;
import com.foros.model.security.User;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.BusinessException;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.util.EntityUtils;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.annotation.Validate;

import java.sql.Timestamp;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityNotFoundException;

@Stateless(name = "AccountsPayableFinanceService")
@Interceptors({RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class})
public class AccountsPayableFinanceServiceBean extends FinanceServiceBase implements AccountsPayableFinanceService {
    private static final String[] READ_ONLY_FIELDS = {
            "account",
            "accountId",
            "taxRate"
    };

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Override
    public AccountsPayableFinancialSettings getFinancialSettings(long accountId) {
        // restriction inherited from find()
        Account account = accountService.view(accountId);
        if (!(account instanceof AccountsPayableAccountBase)) {
            throw new EntityNotFoundException("Account with id=" + accountId + " not found");
        }
        return ((AccountsPayableAccountBase)account).getFinancialSettings();
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Validate(validation = "AccountsPayableAccount.updateFinance", parameters = "#settings")
    @Restrict(restriction = "AccountsPayableAccount.updateFinance", parameters = "find('Account', #settings.id)")
    public void updateFinance(AccountsPayableFinancialSettings settings) {
        AccountsPayableFinancialSettings existingData = em.find(AccountsPayableFinancialSettings.class, settings.getId());
        AccountsPayableAccountBase account = existingData.getAccount();

        // TODO: Move to validation
        if (settings.isChanged("defaultBillToUser") && !account.getUsers().contains(settings.getDefaultBillToUser())) {
            throw new BusinessException("Not permitted default bill to user.");
        }

        settings.unregisterChange(READ_ONLY_FIELDS);

        prePersistFinance(settings, account);

        updateCommissionForZeroOrNull(settings);

        String countryCode = settings.isChanged("bankCountry") ? settings.getBankCountry().getCountryCode() :
                existingData.getBankCountry().getCountryCode();

        if ("GB".equals(countryCode)) {
            settings.setPaymentMethod(PaymentMethod.BACS);
        } else {
            settings.setPaymentMethod(PaymentMethod.Swift);
        }

        EntityUtils.copy(existingData, settings);
        auditService.audit(account, ActionType.UPDATE);
    }

    private void prePersistFinance(AccountsPayableFinancialSettings settings, AccountsPayableAccountBase account) {
        // bank country
        if (settings.isChanged("bankCountry")) {
            String cc = settings.getBankCountry().getCountryCode();
            settings.setBankCountry(em.getReference(Country.class, cc));
        }

        // bank currency
        if (settings.isChanged("bankCurrency")) {
            Currency bankCurrency = em.find(Currency.class, settings.getBankCurrency().getId());
            settings.setBankCurrency(bankCurrency);
        }

        // default bill to user
        if (settings.isChanged("defaultBillToUser")) {
            settings.setDefaultBillToUser(em.getReference(User.class, settings.getDefaultBillToUser().getId()));
        }
    }

    @Override
    public Timestamp getBillingJobNextStartDate() {
        return jdbcTemplate.queryForObject(
                "select * from jobs.get_next_run_timestamp(?::varchar)",
                Timestamp.class,
                "generate_invoices_and_bills"
        );
    }
}
