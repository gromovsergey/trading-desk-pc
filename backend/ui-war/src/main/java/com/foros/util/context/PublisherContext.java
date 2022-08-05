package com.foros.util.context;

import com.foros.model.account.PublisherAccount;

public class PublisherContext extends ContextBase<PublisherAccount> {
    @Override
    protected String getNoContextMessageKey() {
        return "error.context.notset.publisher";
    }

    @Override
    public boolean switchTo(Long accountId) {
        return switchTo(findAccount(PublisherAccount.class, accountId));
    }
}
