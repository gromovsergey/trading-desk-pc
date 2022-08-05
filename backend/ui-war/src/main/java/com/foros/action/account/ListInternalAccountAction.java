package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;
import com.foros.session.security.AccountTO;

import java.util.List;
import javax.ejb.EJB;

public class ListInternalAccountAction extends BaseActionSupport {

    @EJB
    private AccountService accountService;

    private List<AccountTO> accounts;

    @ReadOnly
    public String list() {
        accounts = accountService.search(AccountRole.INTERNAL);

        return SUCCESS;
    }

    public List<AccountTO> getAccounts() {
        return accounts;
    }
}
