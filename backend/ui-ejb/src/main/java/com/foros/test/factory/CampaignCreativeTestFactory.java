package com.foros.test.factory;

import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.session.campaign.CampaignCreativeService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class CampaignCreativeTestFactory extends TestFactory<CampaignCreative> {
    @Autowired
    private CampaignCreativeService campaignCreativeService;

    protected abstract void prepare(CampaignCreative campaignCreative);

    private void populate(CampaignCreative campaignCreative) {
        campaignCreative.setStatus(Status.ACTIVE);
        campaignCreative.setDisplayStatus(CampaignCreative.LIVE);
    }

    @Override
    public CampaignCreative create() {
        return new CampaignCreative();
    }

    @Override
    public void persist(CampaignCreative campaignCreative) {
        campaignCreativeService.create(campaignCreative);
        entityManager.flush();
    }

    public void update(CampaignCreative campaignCreative) {
        campaignCreativeService.update(campaignCreative);
    }

    @Override
    public CampaignCreative createPersistent() {
        CampaignCreative campaignCreative = create();
        prepare(campaignCreative);
        persist(campaignCreative);
        return campaignCreative;
    }

    public CampaignCreative create(Creative creative) {
        CampaignCreative campaignCreative = new CampaignCreative();
        populate(campaignCreative);

        campaignCreative.setCreative(creative);

        return campaignCreative;
    }

    public CampaignCreative create(CampaignCreativeGroup group, Creative creative) {
        CampaignCreative campaignCreative = new CampaignCreative();
        populate(campaignCreative);

        campaignCreative.setCreativeGroup(group);
        campaignCreative.setCreative(creative);

        return campaignCreative;
    }

    public CampaignCreative find(Long id) {
        return findAny(CampaignCreative.class, new QueryParam("id", id));
    }
}
