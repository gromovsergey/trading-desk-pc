package com.foros.rs.client.service;

import com.foros.rs.client.RsClient;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroupSelector;


public class CampaignCreativeGroupService extends EntityServiceSupport<CampaignCreativeGroup, CampaignCreativeGroupSelector> {

    public CampaignCreativeGroupService(RsClient rsClient) {
        super(rsClient, "/creativeGroups");
    }
}