package com.foros.rs.sandbox.factory;

import com.foros.model.action.Action;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.test.factory.DisplayCCGTestFactory;

public class DisplayGroupGeneratorFactory extends BaseGeneratorFactory<CampaignCreativeGroup, DisplayCCGTestFactory> {
    private Campaign campaign;
    private Action conversion;

    public DisplayGroupGeneratorFactory(DisplayCCGTestFactory displayCcgTestFactory, Campaign campaign, Action conversion) {
        super(displayCcgTestFactory);
        this.campaign = campaign;
        this.conversion = conversion;
    }

    @Override
    protected CampaignCreativeGroup createDefault() {
        CampaignCreativeGroup entity = factory.create(campaign);
        entity.setChannelTarget(ChannelTarget.UNTARGETED);
        entity.getActions().add(conversion);
        return entity;
    }
}

