package com.foros.rs.sandbox.factory;

import com.foros.model.account.Account;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.security.AccountType;
import com.foros.test.factory.PublisherAccountTestFactory;
import com.foros.util.FlagsUtil;

public class PublisherAccountGeneratorFactory extends BaseGeneratorFactory<PublisherAccount, PublisherAccountTestFactory> {
    private InternalAccount internalAccount;
    private AccountType publisherAccountType;

    public PublisherAccountGeneratorFactory(PublisherAccountTestFactory accountTestFactory, InternalAccount internalAccount,
                                            AccountType publisherAccountType) {
        super(accountTestFactory);
        this.internalAccount = internalAccount;
        this.publisherAccountType = publisherAccountType;
    }

    @Override
    protected PublisherAccount createDefault() {
        PublisherAccount entity = factory.create(publisherAccountType, internalAccount);
        entity.setFlags(FlagsUtil.set(entity.getFlags(), Account.REFERRER_REPORT_FLAG, true));
        entity.setFlags(FlagsUtil.set(entity.getFlags(), Account.PUB_ADVERTISING_REPORT_FLAG, true));
        entity.setCreativesReapproval(true);
        return entity;
    }
}
