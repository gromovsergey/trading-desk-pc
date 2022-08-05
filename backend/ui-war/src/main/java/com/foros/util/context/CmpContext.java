package com.foros.util.context;

import com.foros.model.account.CmpAccount;

public class CmpContext extends ContextBase<CmpAccount> {
    @Override
    protected String getNoContextMessageKey() {
        return "error.context.notset.cmp";
    }

    @Override
    public boolean switchTo(Long accountId) {
        return switchTo(findAccount(CmpAccount.class, accountId));
    }
}
