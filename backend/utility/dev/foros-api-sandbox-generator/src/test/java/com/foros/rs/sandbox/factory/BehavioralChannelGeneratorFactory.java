package com.foros.rs.sandbox.factory;

import com.foros.model.Country;
import com.foros.model.account.AgencyAccount;
import com.foros.model.channel.BehavioralChannel;
import com.foros.session.channel.service.BehavioralChannelService;
import com.foros.test.factory.BehavioralChannelTestFactory;

public class BehavioralChannelGeneratorFactory extends BaseGeneratorFactory<BehavioralChannel, BehavioralChannelTestFactory> {
    private BehavioralChannelService behavioralChannelService;
    private AgencyAccount agencyAccount;

    public BehavioralChannelGeneratorFactory(BehavioralChannelTestFactory behavioralChannelTestFactory,
            BehavioralChannelService behavioralChannelService, AgencyAccount agencyAccount) {
        super(behavioralChannelTestFactory);
        this.behavioralChannelService = behavioralChannelService;
        this.agencyAccount = agencyAccount;
    }

    @Override
    protected BehavioralChannel createDefault() {
        BehavioralChannel entity = factory.create(agencyAccount);
        entity.setAccount(agencyAccount);
        entity.setCountry(new Country("GB"));
        return entity;
    }

    @Override
    protected void updatePersistentEntity(BehavioralChannel entity) {
        try {
            factory.makeLive(entity);
            behavioralChannelService.makePublic(entity.getId(), entity.getVersion());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
