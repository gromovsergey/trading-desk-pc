package com.foros.session.site;

import com.foros.model.Status;
import com.foros.model.site.Site;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;

import java.util.List;

public class SiteSelector implements Selector<Site> {

    private List<Long> accountIds;
    private List<Long> siteIds;
    private List<Status> siteStatuses;
    private Paging paging;

    public List<Long> getAccountIds() {
        return accountIds;
    }

    public List<Long> getSiteIds() {
        return siteIds;
    }

    public List<Status> getSiteStatuses() {
        return siteStatuses;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private List<Long> accountIds;
        private List<Long> siteIds;
        private List<Status> siteStatuses;
        private Paging paging;

        public Builder accountIds(List<Long> accountIds) {
            this.accountIds = accountIds;
            return this;
        }

        public Builder siteIds(List<Long> siteIds) {
            this.siteIds = siteIds;
            return this;
        }

        public Builder siteStatuses(List<Status> siteStatuses) {
            this.siteStatuses = siteStatuses;
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public SiteSelector build() {
            return new SiteSelector(this);
        }
    }

    private SiteSelector(Builder builder) {
        this.accountIds = builder.accountIds;
        this.siteIds = builder.siteIds;
        this.siteStatuses = builder.siteStatuses;
        this.paging = builder.paging;
    }
}
