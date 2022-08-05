package com.foros.action.account;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.security.User;
import com.foros.session.finance.AccountsPayableFinanceService;
import com.foros.session.security.UserService;
import com.foros.util.StringUtil;
import com.foros.util.context.ContextBase;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ViewAccountActionBase<T extends Account> extends AccountActionBase<T> implements RequestContextsAware {
    @EJB
    protected UserService userService;

    @EJB
    private AccountsPayableFinanceService accountsPayableFinanceService;

    private List<User> accountUsers;

    public List<User> getAccountUsers() {
        if (accountUsers != null) {
            return accountUsers;
        }

        accountUsers = accountService.findAccountUsers(account.getId());
        Collections.sort(accountUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return StringUtil.compareToIgnoreCase(o1.getFullName(), o2.getFullName());
            }
        });

        return accountUsers;
    }

    public Date getBillingTime() {
        return accountsPayableFinanceService.getBillingJobNextStartDate();
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        ContextBase context = contexts.getContext(account.getRole());

        if (context != null) {
            context.switchTo(account.getId());
        }
    }
}
