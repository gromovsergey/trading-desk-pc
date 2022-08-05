package com.foros.rs.sandbox.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.test.factory.TextCampaignTestFactory;

public class TextCampaignGeneratorFactory extends BaseGeneratorFactory<Campaign, TextCampaignTestFactory> {
    private AdvertiserAccount advertiserAccount;

    public TextCampaignGeneratorFactory(TextCampaignTestFactory textCampaignTestFactory, AdvertiserAccount advertiserAccount) {
        super(textCampaignTestFactory);
        this.advertiserAccount = advertiserAccount;
    }

    @Override
    protected Campaign createDefault() {
        Campaign entity = factory.create(advertiserAccount);
        entity.setAccount(advertiserAccount);
        return entity;
    }
}
