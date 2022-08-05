package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.AccountService;

public interface ForosAccountService {
    AccountService getAccountService();
    AccountService getAdminAccountService();
}
