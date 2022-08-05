package com.foros.session.campaign.bulk;

import java.util.List;

public class CreativeLinkSelector extends CampaignCreativeGroupSelector {

    private List<Long> creatives;
    private List<Long> creativeLinks;

    public List<Long> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<Long> creatives) {
        this.creatives = creatives;
    }

    public List<Long> getCreativeLinks() {
        return creativeLinks;
    }

    public void setCreativeLinks(List<Long> creativeLinks) {
        this.creativeLinks = creativeLinks;
    }
}
