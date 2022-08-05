package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.site.Site;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class RemoveSitesOperation extends SitesOperationSupport {
    private final Collection<Site> allSites;

    public RemoveSitesOperation(Collection<Site> sites, Collection<Site> allSites) {
        super(sites);
        this.allSites = allSites;
    }

    @Override
    public void perform(CampaignCreativeGroup existing, CampaignCreativeGroup toUpdate) {
        Set<Site> newSites = new TreeSet<>(BY_ID_COMPARATOR);
        if (existing.isIncludeSpecificSitesFlag()) {
            newSites.addAll(existing.getSites());
        } else {
            toUpdate.setIncludeSpecificSitesFlag(true);
            newSites.addAll(allSites);
        }
        newSites.removeAll(sites);
        toUpdate.setSites(newSites);
    }
}
