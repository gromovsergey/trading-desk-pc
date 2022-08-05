package com.foros.session.reporting.dashboard;

import java.math.BigDecimal;

public class PublisherDashboardTO {

    private long creditedImps;

    private long imps;

    private long clicks;

    private BigDecimal ctr;

    private long requests;

    private BigDecimal ecpm;

    private BigDecimal revenue;

    protected PublisherDashboardTO(Builder builder) {
        this.creditedImps = builder.creditedImps;
        this.imps = builder.imps;
        this.clicks = builder.clicks;
        this.ctr = builder.ctr;
        this.requests = builder.requests;
        this.ecpm = builder.ecpm;
        this.revenue = builder.revenue;
    }

    protected static class Builder {
        private long creditedImps;

        private long imps;

        private long clicks;

        private BigDecimal ctr;

        private long requests;

        private BigDecimal ecpm;

        private BigDecimal revenue;

        public Builder creditedImps(long creditedImps) {
            this.creditedImps = creditedImps;
            return this;
        }

        public Builder imps(long imps) {
            this.imps = imps;
            return this;
        }

        public Builder clicks(long clicks) {
            this.clicks = clicks;
            return this;
        }

        public Builder ctr(BigDecimal ctr) {
            this.ctr = ctr;
            return this;
        }

        public Builder requests(long requests) {
            this.requests = requests;
            return this;
        }

        public Builder ecpm(BigDecimal ecpm) {
            this.ecpm = ecpm;
            return this;
        }

        public Builder revenue(BigDecimal revenue) {
            this.revenue = revenue;
            return  this;
        }

        public PublisherDashboardTO build() {
            return new PublisherDashboardTO(this);
        }
    }



    public long getCreditedImps() {
        return creditedImps;
    }

    public long getImps() {
        return imps;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public BigDecimal getCtr() {
        return ctr;
    }

    public void setCtr(BigDecimal ctr) {
        this.ctr = ctr;
    }

    public BigDecimal getEcpm() {
        return ecpm;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public long getRequests() {
        return requests;
    }

    public void setRequests(long requests) {
        this.requests = requests;
    }
}
