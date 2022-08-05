package app.programmatic.ui.common.testtools;

import static app.programmatic.ui.common.config.TestEnvironment.ACCOUNT_NAME_TEMPLATE;

import app.programmatic.ui.account.dao.model.AccountRoleParam;
import app.programmatic.ui.account.dao.model.AccountStats;
import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.common.foros.service.TestCurUserTokenKeyService;

import java.time.LocalDateTime;

public class TestEnvironment {
    public static TestEnvironmentVariables initialize(TestCurUserTokenKeyService curUserTokenKeyService,
                                                      SearchAccountService searchAccountService) {
        curUserTokenKeyService.configureServicesForAdmin();

        Long agencyId = null;
        Long accountId = null;

        AccountStats accountStats = searchAccountService
                .searchAdvertisingAccounts(ACCOUNT_NAME_TEMPLATE, null, null, AccountRoleParam.ALL).get(0);
        if (accountStats.isAgency()) {
            agencyId = accountStats.getId();
            accountId = searchAccountService
                    .searchInAgencyAdvertisingAccounts(agencyId).get(0).getAdvertiserId();
        } else {
            accountId = accountStats.getId();
        }

        return new TestEnvironmentVariables(agencyId, accountId, LocalDateTime.now());
    }


}
