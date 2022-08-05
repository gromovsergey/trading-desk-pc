package com.foros.session.finance;

import com.foros.AbstractServiceBeanIntegrationTest;
import com.foros.model.account.AccountsPayableFinancialSettings;
import com.foros.model.account.PublisherAccount;
import com.foros.test.factory.PublisherAccountTestFactory;

import group.Db;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(Db.class)
public class AccountsPayableFinanceServiceBeanTest extends AbstractServiceBeanIntegrationTest {
    @Autowired
    private AccountsPayableFinanceService financeService;

    @Autowired
    private PublisherAccountTestFactory publisherAccountTF;

    @Test
    public void testUpdateFinance() {
        PublisherAccount account = publisherAccountTF.createPersistent();
        AccountsPayableFinancialSettings settings = account.getFinancialSettings();
        String newBicCode = "202020";
        settings.setBankBicCode(newBicCode);
        financeService.updateFinance(settings);
        commitChanges();

        settings = getEntityManager().find(AccountsPayableFinancialSettings.class, settings.getId());
        assertEquals(newBicCode, settings.getBankBicCode());
    }

    @Test
    public void testUpdateHandlingFee() {
        PublisherAccount account = publisherAccountTF.createPersistent();
        AccountsPayableFinancialSettings settings = account.getFinancialSettings();
        AccountsPayableFinancialSettings financialSettings = publisherAccountTF.createFinancialSettings(account);
        BigDecimal commission = new BigDecimal(0L);

        financialSettings.setId(settings.getId());
        financialSettings.setAccount(settings.getAccount());
        financialSettings.setCommission(commission);
        financialSettings.setDefaultBillToUser(settings.getDefaultBillToUser());
        financialSettings.setBankCountry(settings.getBankCountry());
        financialSettings.setBankCurrency(settings.getBankCurrency());

        financeService.updateFinance(financialSettings);
        getEntityManager().flush();

        getEntityManager().clear();
        settings = getEntityManager().find(AccountsPayableFinancialSettings.class, settings.getId());
        assertTrue(commission.compareTo(settings.getCommission()) == 0);
    }

//    // ToDo: uncomment when needed (OUI-28825)
//    @Test
//    public void testGetBillingJobNextStartDate() {
//        Date nextStartDate = financeService.getBillingJobNextStartDate();
//        assertNotNull(nextStartDate);
//    }
}
