package com.foros.rs.sandbox.factory;

import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsApprovalType;
import com.foros.model.security.AdvExclusionsType;
import com.foros.test.factory.PublisherAccountTypeTestFactory;

public class PublisherAccountTypeGeneratorFactory  extends BaseGeneratorFactory<AccountType, PublisherAccountTypeTestFactory> {
    private CreativeSize size;

    public PublisherAccountTypeGeneratorFactory(PublisherAccountTypeTestFactory accountTypeTestFactory, CreativeSize size) {
        super(accountTypeTestFactory);
        this.size = size;
    }

    @Override
    public AccountType findOrCreate(String entityName) {
        AccountType accountType = super.findOrCreate(entityName);
        accountType.setAdvExclusions(AdvExclusionsType.SITE_AND_TAG_LEVELS);
        accountType.setAdvExclusionApproval(AdvExclusionsApprovalType.ACCEPTED);
        return accountType;
    }

    @Override
    protected AccountType createDefault() {
        return factory.create(size, null);
    }
}
