package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.campaign.CampaignCreativeGroup;

public class CampaignCreativeGroupXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        CampaignCreativeGroup ccg = new CampaignCreativeGroup();
        ccg.setId(id);
        return ccg;
    }

}
