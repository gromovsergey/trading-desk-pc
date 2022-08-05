package com.foros.session.query.campaign;

import com.foros.model.campaign.CampaignType;
import com.foros.session.query.AdvertiserEntityQuery;

import java.util.Collection;

public interface CampaignCreativeGroupQuery extends AdvertiserEntityQuery<CampaignCreativeGroupQuery> {

    CampaignCreativeGroupQuery campaigns(Collection<Long> ids);

    CampaignCreativeGroupQuery creativeGroups(Collection<Long> ids);

    CampaignCreativeGroupQuery geoChannels();

    CampaignCreativeGroupQuery geoChannelsExcluded();

    CampaignCreativeGroupQuery colocations();

    CampaignCreativeGroupQuery sites();

    CampaignCreativeGroupQuery type(CampaignType campaignType);

    CampaignCreativeGroupQuery keyword();

    CampaignCreativeGroupQuery actions(Collection<Long> ids);
}
