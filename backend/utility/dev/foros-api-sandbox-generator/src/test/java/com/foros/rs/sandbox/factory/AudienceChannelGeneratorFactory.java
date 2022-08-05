package com.foros.rs.sandbox.factory;

import com.foros.model.Country;
import com.foros.model.account.InternalAccount;
import com.foros.model.channel.AudienceChannel;
import com.foros.test.factory.AudienceChannelTestFactory;

public class AudienceChannelGeneratorFactory extends BaseGeneratorFactory<AudienceChannel, AudienceChannelTestFactory> {
    private InternalAccount internalAccount;

    public AudienceChannelGeneratorFactory(AudienceChannelTestFactory audienceChannelTestFactory, InternalAccount internalAccount) {
        super(audienceChannelTestFactory);
        this.internalAccount = internalAccount;
    }

    @Override
    protected AudienceChannel createDefault() {
        AudienceChannel entity = factory.create(internalAccount);
        entity.setAccount(internalAccount);
        entity.setCountry(new Country("GB"));
        return entity;
    }
}
