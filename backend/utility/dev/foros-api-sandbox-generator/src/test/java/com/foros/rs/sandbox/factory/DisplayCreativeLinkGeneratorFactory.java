package com.foros.rs.sandbox.factory;

import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.test.factory.DisplayCreativeLinkTestFactory;
import com.foros.test.factory.QueryParam;

public class DisplayCreativeLinkGeneratorFactory extends BaseGeneratorFactory<CampaignCreative, DisplayCreativeLinkTestFactory> {
    private Creative creative;
    private CampaignCreativeGroup group;

    public DisplayCreativeLinkGeneratorFactory(DisplayCreativeLinkTestFactory displayCreativeLinkTestFactory, CampaignCreativeGroup group) {
        super(displayCreativeLinkTestFactory);
        this.group = group;
    }

    public CampaignCreative findOrCreate(String entityName, Creative creative) {
        this.creative = creative;
        return  findOrCreate(entityName);
    }

    @Override
    protected CampaignCreative createDefault() {
        return factory.create(group, creative);
    }

    @Override
    protected QueryParam getFindByNameQuery(String name) {
        return new QueryParam("creative.id", creative.getId());
    }

    @Override
    protected void setName(CampaignCreative entity, String name) {
    }
}
