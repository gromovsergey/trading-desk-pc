package com.foros.session.reporting.dashboard;

import java.math.BigDecimal;

public class SiteDashboardTO extends PublisherDashboardTO {

    private long siteId;

    private String siteName;

    private String siteUrl;

    private int siteDisplayStatusId;

    private boolean tagExist;

    private int creativesToApprove;

    public SiteDashboardTO(Builder builder) {
        super(builder.parent);
        this.siteId = builder.siteId;
        this.siteName = builder.siteName;
        this.siteUrl = builder.siteUrl;
        this.siteDisplayStatusId = builder.siteDisplayStatusId;
        this.tagExist = builder.tagExist;
        this.creativesToApprove = builder.creativesToApprove;
    }

    public static class Builder {

        private long siteId;

        private String siteName;

        private String siteUrl;

        private int siteDisplayStatusId;

        private boolean tagExist;

        private int creativesToApprove;

        private PublisherDashboardTO.Builder parent;

        public Builder() {
            parent = new PublisherDashboardTO.Builder();
        }

        public SiteDashboardTO build() {
            return new SiteDashboardTO(this);
        }

        public Builder creativesToApprove(int creativesToApprove) {
            this.creativesToApprove = creativesToApprove;
            return this;
        }

        public Builder siteId(long siteId) {
            this.siteId = siteId;
            return this;
        }

        public Builder siteName(String siteName) {
            this.siteName = siteName;
            return this;
        }

        public Builder siteUrl(String siteUrl) {
            this.siteUrl = siteUrl;
            return this;
        }

        public Builder siteDisplayStatusId(int siteDisplayStatusId) {
            this.siteDisplayStatusId = siteDisplayStatusId;
            return this;
        }

        public Builder tagExist(boolean tagExist) {
            this.tagExist = tagExist;
            return this;
        }

        public Builder imps(long imps) {
            parent.imps(imps);
            return this;
        }

        public Builder creditedImps(long creditedImps) {
            parent.creditedImps(creditedImps);
            return this;
        }

        public Builder clicks(long clicks) {
            parent.clicks(clicks);
            return this;
        }

        public Builder ctr(BigDecimal ctr) {
            parent.ctr(ctr);
            return this;
        }

        public Builder requests(long requests) {
            parent.requests(requests);
            return this;
        }

        public Builder ecpm(BigDecimal ecpm) {
            parent.ecpm(ecpm);
            return this;
        }

        public Builder revenue(BigDecimal revenue) {
            parent.revenue(revenue);
            return this;
        }
    }

    public long getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public int getSiteDisplayStatusId() {
        return siteDisplayStatusId;
    }

    public boolean isTagExist() {
        return tagExist;
    }

    public int getCreativesToApprove() {
        return creativesToApprove;
    }
}
