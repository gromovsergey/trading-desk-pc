package com.foros.session.reporting.dashboard;

import java.math.BigDecimal;

public class TagDashboardTO extends PublisherDashboardTO {

    private long tagId;

    private String tagName;

    private String tagSizeName;

    private char status;

    private TagDashboardTO(Builder builder) {
        super(builder.parent);
        this.tagId = builder.tagId;
        this.tagName = builder.tagName;
        this.tagSizeName = builder.tagSizeName;
        this.status = builder.status;
    }

    public long getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagSizeName() {
        return tagSizeName;
    }

    public char getStatus() {
        return status;
    }

    public static class Builder {
        private long tagId;

        private String tagName;

        private String tagSizeName;

        private char status;

        private PublisherDashboardTO.Builder parent;

        public Builder() {
            parent = new PublisherDashboardTO.Builder();
        }

        public Builder tagId(long tagId) {
            this.tagId = tagId;
            return this;

        }

        public Builder tagName(String tagName) {
            this.tagName = tagName;
            return this;
        }

        public Builder tagSizeName(String tagSizeName) {
            this.tagSizeName = tagSizeName;
            return this;
        }

        public Builder status(char status) {
            this.status = status;
            return this;
        }

        public Builder creditedImps(long creditedImps) {
            parent.creditedImps(creditedImps);
            return this;
        }

        public Builder requests(long requests) {
            parent.requests(requests);
            return this;
        }

        public Builder revenue(BigDecimal revenue) {
            parent.revenue(revenue);
            return this;
        }

        public Builder ctr(BigDecimal ctr) {
            parent.ctr(ctr);
            return this;
        }

        public Builder imps(long imps) {
            parent.imps(imps);
            return this;
        }

        public Builder ecpm(BigDecimal ecpm) {
            parent.ecpm(ecpm);
            return this;
        }

        public Builder clicks(long clicks) {
            parent.clicks(clicks);
            return this;
        }

        public TagDashboardTO build() {
            return new TagDashboardTO(this);
        }
    }
}
