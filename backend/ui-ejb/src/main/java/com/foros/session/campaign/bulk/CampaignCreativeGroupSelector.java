package com.foros.session.campaign.bulk;

import java.util.List;

public class CampaignCreativeGroupSelector extends CampaignSelector {

    private List<Long> creativeGroups;

    public List<Long> getCreativeGroups() {
        return creativeGroups;
    }

    public void setCreativeGroups(List<Long> creativeGroups) {
        this.creativeGroups = creativeGroups;
    }

}