package com.foros.util.context;

import com.foros.model.account.IspAccount;

public class IspContext extends ContextBase<IspAccount> {
    @Override
    protected String getNoContextMessageKey() {
        return "error.context.notset.isp";
    }

    @Override
    public boolean switchTo(Long accountId) {
        return switchTo(findAccount(IspAccount.class, accountId));
    }
}
