package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.site.Site;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SetSitesOperation extends SitesOperationSupport {

    private final boolean includeSpecificSitesFlag;

    public SetSitesOperation(Collection<Site> sites, boolean includeSpecificSitesFlag) {
        super(sites);
        this.includeSpecificSitesFlag = includeSpecificSitesFlag;
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        if (includeSpecificSitesFlag) {
            Set<Site> toUpdateSites = toUpdate.getSites();
            toUpdateSites.clear();
            toUpdateSites.addAll(sites);
            toUpdate.setIncludeSpecificSitesFlag(true);
        } else {
            if (!existing.getSites().isEmpty()) {
                toUpdate.setSites(new HashSet<Site>());
            }
            toUpdate.setIncludeSpecificSitesFlag(false);
        }
    }

    public boolean isIncludeSpecificSitesFlag() {
        return includeSpecificSitesFlag;
    }
}
