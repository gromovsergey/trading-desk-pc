package com.foros.session.finance;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingFinancialSettings;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.currency.Currency;
import com.foros.model.security.User;
import com.foros.model.security.UserRole;
import com.foros.security.MockPrincipal;
import com.foros.service.mock.AdvertisingFinanceServiceMock;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.test.factory.AdvertiserAccountTestFactory;
import com.foros.test.factory.AgencyAccountTestFactory;
import com.foros.test.factory.CurrencyTestFactory;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.test.factory.InvoiceTestFactory;
import com.foros.test.factory.UserTestFactory;
import com.foros.util.StringUtil;

import group.Db;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class AdvertisingFinanceServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private AdvertisingFinanceServiceMock financeService;

    @Autowired
    private InvoiceTestFactory invoiceTF;

    @Autowired
    private AgencyAccountTestFactory agencyAccountTF;

    @Autowired
    private AdvertiserAccountTestFactory advertiserAccountTF;

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private UserTestFactory userTF;

    @Autowired
    private CurrencyTestFactory currencyTF;

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Test
    public void testUpdateCommission() {
        AgencyAccount account = agencyAccountTF.createPersistent();
        AdvertisingFinancialSettings settings = account.getFinancialSettings();
        AdvertisingFinancialSettings financialSettings = new AdvertisingFinancialSettings();
        BigDecimal commission = new BigDecimal(0L);

        financialSettings.setId(settings.getId());
        financialSettings.setAccountId(settings.getId());
        financialSettings.setAccount(settings.getAccount());
        financialSettings.setCommission(commission);
        financialSettings.setVersion(settings.getVersion());
        financialSettings.getData().setVersion(settings.getData().getVersion());
        financeService.updateFinance(financialSettings);

        commitChanges();
        settings = getEntityManager().find(AdvertisingFinancialSettings.class, settings.getId());
        assertTrue(commission.compareTo(settings.getCommission()) == 0);
    }

    @Test
    public void testUpdateCommissionFOrStandalone() {
        AdvertiserAccount account = advertiserAccountTF.createPersistent();
        AdvertisingFinancialSettings settings = account.getFinancialSettings();
        AdvertisingFinancialSettings financialSettings = new AdvertisingFinancialSettings();
        BigDecimal commission = new BigDecimal(0L);

        financialSettings.setId(settings.getId());
        financialSettings.setAccountId(settings.getId());
        financialSettings.setAccount(settings.getAccount());
        financialSettings.setCommission(commission);
        financialSettings.setVersion(settings.getVersion());
        financialSettings.getData().setVersion(settings.getData().getVersion());
        try {
            financeService.updateFinance(financialSettings);
            assertTrue(false);
        } catch (SecurityException e) {
            // do nothing
        }
    }

    @Test
    public void testIsPrepaidAmountValid() {
        Currency currencyUSD = currencyTF.findOrCreatePersistent("USD");
        Currency currencyRUB = currencyTF.findOrCreatePersistent("RUB");
        AdvertiserAccount accountUS = advertiserAccountTF.create();
        accountUS.setCurrency(currencyUSD);
        advertiserAccountTF.persist(accountUS);

        AdvertiserAccount accountRUB = advertiserAccountTF.create();
        accountRUB.setCurrency(currencyRUB);
        advertiserAccountTF.persist(accountRUB);

        InternalAccount internalAccount = internalAccountTF.createPersistent();
        UserRole userRole = getEntityManager().find(UserRole.class, 9L);
        User user = userTF.createPersistent(internalAccount, userRole);
        user.setMaxCreditLimit(BigDecimal.ZERO);
        userTF.update(user);
        currentUserRule.setPrincipal(
             new MockPrincipal(user.getEmail(), user.getId(), user.getAccount().getId(), user.getRole().getId(),
                         Long.parseLong(StringUtil.toString(user.getRole().getAccountRole().getId()))
             )
        );
        assertTrue(financeService.isCreditLimitValid(accountUS.getId(), new BigDecimal(20)));
        assertTrue(financeService.isCreditLimitValid(accountUS.getId(), null));

        user.setMaxCreditLimit(new BigDecimal(1234));
        userTF.update(user);
        CurrencyConverter currencyConverter = currencyExchangeService.getCrossRate(accountRUB.getCurrency().getId(), new Date());
        BigDecimal convertedPrepaidAmount = currencyConverter.convert(currencyUSD.getId(), user.getMaxCreditLimit());

        assertTrue(financeService.isCreditLimitValid(accountRUB.getId(), convertedPrepaidAmount.subtract(new BigDecimal(20))));
        assertTrue(financeService.isCreditLimitValid(accountRUB.getId(), null));
        assertTrue(financeService.isCreditLimitValid(accountRUB.getId(), convertedPrepaidAmount));
        assertFalse(financeService.isCreditLimitValid(accountRUB.getId(), convertedPrepaidAmount.add(new BigDecimal(1))));
    }

    @Test
    public void testGetCreditBalance() {
        AdvertiserAccount acc = advertiserAccountTF.createPersistent();
        BigDecimal balance = financeService.getCreditBalance(acc.getId());
        assertNotNull(balance);
        assertTrue(balance.compareTo(BigDecimal.TEN) == 0);
    }
}
