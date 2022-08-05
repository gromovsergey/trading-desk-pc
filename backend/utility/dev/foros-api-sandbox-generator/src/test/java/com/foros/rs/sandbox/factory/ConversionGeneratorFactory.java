package com.foros.rs.sandbox.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.test.factory.ActionTestFactory;

public class ConversionGeneratorFactory extends BaseGeneratorFactory<Action, ActionTestFactory> {
    private AdvertiserAccount advertiserAccount;

    public ConversionGeneratorFactory(ActionTestFactory actionTestFactory, AdvertiserAccount advertiserAccount) {
        super(actionTestFactory);
        this.advertiserAccount = advertiserAccount;
    }

    @Override
    protected Action createDefault() {
        return factory.create(advertiserAccount);
    }
}
