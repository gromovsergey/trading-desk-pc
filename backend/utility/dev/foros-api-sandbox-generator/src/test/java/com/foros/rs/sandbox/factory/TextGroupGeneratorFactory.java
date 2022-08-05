package com.foros.rs.sandbox.factory;

import com.foros.model.action.Action;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.test.factory.TextCCGTestFactory;

public class TextGroupGeneratorFactory extends BaseGeneratorFactory<CampaignCreativeGroup, TextCCGTestFactory> {
    private Campaign campaign;
    private Action conversion;

    public TextGroupGeneratorFactory(TextCCGTestFactory textCcgTestFactory, Campaign campaign, Action conversion) {
        super(textCcgTestFactory);
        this.campaign = campaign;
        this.conversion = conversion;
    }

    @Override
    protected CampaignCreativeGroup createDefault() {
        CampaignCreativeGroup entity = factory.create(campaign);
        entity.getActions().add(conversion);
        return entity;
    }
}
