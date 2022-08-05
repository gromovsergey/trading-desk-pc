package com.foros.rs.sandbox.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.test.factory.AdvertiserAccountTestFactory;

public class AdvertiserAccountGeneratorFactory extends BaseGeneratorFactory<AdvertiserAccount, AdvertiserAccountTestFactory> {
    private AgencyAccount agencyAccount;

    public AdvertiserAccountGeneratorFactory(AdvertiserAccountTestFactory accountTestFactory, AgencyAccount agencyAccount) {
        super(accountTestFactory);
        this.agencyAccount = agencyAccount;
    }

    @Override
    protected AdvertiserAccount createDefault() {
        return factory.createAdvertiserInAgency(agencyAccount);
    }

    @Override
    protected void persist(AdvertiserAccount entity) {
        factory.persistAgencyAdvertiser(entity);
    }

    @Override
    protected void update(AdvertiserAccount entity, AdvertiserAccount existing) {
        factory.updateAgencyAdvertiser(entity);
    }
}

