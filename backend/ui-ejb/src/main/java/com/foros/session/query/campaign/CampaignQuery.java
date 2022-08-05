package com.foros.session.query.campaign;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.session.query.AdvertiserEntityQuery;

import java.util.Collection;

public interface CampaignQuery extends AdvertiserEntityQuery<CampaignQuery> {

    CampaignQuery campaigns(Collection<Long> ids);

    CampaignQuery type(CampaignType campaignType);

    CampaignQuery existingByName(Collection<Campaign> campaigns);
}
