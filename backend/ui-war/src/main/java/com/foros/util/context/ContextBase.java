package com.foros.util.context;

import com.foros.action.account.ContextNotSetException;
import com.foros.model.account.Account;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.util.ReflectionUtil;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;

public abstract class ContextBase<T extends Account> implements Serializable {
    private Long accountId;
    private transient SwitchContextListener listener = NullSwitchContextListener.INSTANCE;

    SwitchContextListener getListener() {
        return listener == null ? NullSwitchContextListener.INSTANCE : listener;
    }

    void setListener(SwitchContextListener listener) {
        this.listener = listener;
    }

    public boolean switchTo(Long accountId) {
        return switchToInternal(accountId);
    }

    public boolean switchTo(T account) {
        return account == null ? switchToInternal(null) : switchToInternal(account.getId());
    }

    private boolean switchToInternal(Long accountId) {
        if (isSwitchedToInternal(accountId)) {
            return false;
        }
        checkCanBeSwitched(accountId);
        this.accountId = accountId;

        if (accountId != null) {
            getListener().onSwitchTo(this);
        }

        return true;
    }

    protected void checkCanBeSwitched(Long accountId) {
        if (SecurityContext.isInternal()) {
            return;
        }

        if (!SecurityContext.getPrincipal().getAccountId().equals(accountId)) {
            throw new SecurityException("Can't switch to account id: " + accountId);
        }
    }

    public void clear() {
        switchToInternal(null);
    }

    public boolean isSet() {
        return accountId != null;
    }

    public Long getAccountId() {
        checkSet();
        return accountId ;
    }

    public T getAccount() {
        @SuppressWarnings({"unchecked"})
        Class<? extends T> clazz = (Class<? extends T>) ReflectionUtil.getActualTypeArgument(getClass(), 0);
        return findAccount(clazz, getAccountId());
    }

    protected <T extends Account> T findAccount(Class<? extends T> clazz, Long accountId) {
        AccountService accountService = ServiceLocator.getInstance().lookup(AccountService.class);
        return accountService.findForSwitching(clazz, accountId);
    }

    protected void checkSet() {
        if (accountId == null) {
            throw new ContextNotSetException(getNoContextMessageKey());
        }
    }

    protected boolean isSwitchedTo(Long id) {
        return isSwitchedToInternal(id);
    }

    private boolean isSwitchedToInternal(Long id) {
        return ObjectUtils.equals(accountId, id);
    }

    protected abstract String getNoContextMessageKey();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContextBase context = (ContextBase) o;

        if (accountId != null ? !accountId.equals(context.accountId) : context.accountId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return accountId != null ? accountId.hashCode() : 0;
    }
}
