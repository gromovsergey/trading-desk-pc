package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.campaign.Campaign;

public class CampaignXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        Campaign campaign = new Campaign();
        campaign.setId(id);
        return campaign;
    }

}
