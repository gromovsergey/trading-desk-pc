package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.site.Site;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class AddSitesOperation extends SitesOperationSupport {
    public AddSitesOperation(Collection<Site> sites) {
        super(sites);
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        if (!existing.isIncludeSpecificSitesFlag()) {
            return;
        }

        Set<Site> newSites = new TreeSet<>(BY_ID_COMPARATOR);
        newSites.addAll(existing.getSites());
        newSites.addAll(sites);
        toUpdate.setSites(newSites);
    }
}
