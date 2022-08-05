package com.foros.rs.sandbox.factory;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.test.factory.DisplayCampaignTestFactory;

public class DisplayCampaignGeneratorFactory extends BaseGeneratorFactory<Campaign, DisplayCampaignTestFactory> {
    private AdvertiserAccount advertiserAccount;

    public DisplayCampaignGeneratorFactory(DisplayCampaignTestFactory displayCampaignTestFactory, AdvertiserAccount advertiserAccount) {
        super(displayCampaignTestFactory);
        this.advertiserAccount = advertiserAccount;
    }

    @Override
    protected Campaign createDefault() {
        return factory.create(advertiserAccount);
    }
}

