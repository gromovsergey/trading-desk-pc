package com.foros.session.campaign.ccg.bulk;

import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.site.Site;
import com.foros.session.bulk.BulkOperation;

import java.util.Collection;
import java.util.Comparator;

public abstract class SitesOperationSupport implements BulkOperation<CampaignCreativeGroup> {
    protected static final Comparator<Site> BY_ID_COMPARATOR = new Comparator<Site>() {
        @Override
        public int compare(Site o1, Site o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };

    protected Collection<Site> sites;

    public SitesOperationSupport(Collection<Site> sites) {
        this.sites = sites;
    }

    public Collection<Site> getSites() {
        return sites;
    }
}
