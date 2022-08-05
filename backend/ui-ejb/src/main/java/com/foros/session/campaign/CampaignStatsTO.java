package com.foros.session.campaign;

import java.io.Serializable;
import java.math.BigDecimal;

public class CampaignStatsTO implements Serializable {
    private BigDecimal availableBudget;
    private BigDecimal spentBudget;
    private ImpClickStatsTO impClick;
    private long totalUniqueUsers;

    public BigDecimal getAvailableBudget() {
        return availableBudget;
    }

    public BigDecimal getSpentBudget() {
        return spentBudget;
    }

    public long getTotalUniqueUsers() {
        return totalUniqueUsers;
    }

    public static class Builder {
        private BigDecimal availableBudget;
        private BigDecimal spentBudget;
        private long imps;
        private long clicks;
        private long postImpConv;
        private boolean isShowPostImpConv;
        private long postClickConv;
        private boolean isShowPostClickConv;
        private long totalUniqueUsers;

        public Builder availableBudget(BigDecimal availableBudget) {
            this.availableBudget = availableBudget;
            return this;
        }

        public Builder spentBudget(BigDecimal spentBudget) {
            this.spentBudget = spentBudget;
            return this;
        }

        public Builder spentBudget(boolean isGross, BigDecimal cost, BigDecimal commission) {
            this.spentBudget = isGross ? cost.add(commission) : cost;
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

        public Builder postImpConv(long postImpConv) {
            this.postImpConv = postImpConv;
            return this;
        }

        public Builder isShowPostImpConv(boolean isShowPostImpConv) {
            this.isShowPostImpConv = isShowPostImpConv;
            return this;
        }

        public Builder postClickConv(long postClickConv) {
            this.postClickConv = postClickConv;
            return this;
        }

        public Builder isShowPostClickConv(boolean isShowPostClickConv) {
            this.isShowPostClickConv = isShowPostClickConv;
            return this;
        }

        public Builder totalUniqueUsers(long totalUniqueUsers) {
            this.totalUniqueUsers = totalUniqueUsers;
            return this;
        }

        public CampaignStatsTO build() {
            return new CampaignStatsTO(this);
        }
    }

    private CampaignStatsTO(Builder builder) {
        this.availableBudget = builder.availableBudget;
        this.spentBudget = builder.spentBudget;
        this.totalUniqueUsers = builder.totalUniqueUsers;
        this.impClick = new ImpClickStatsTO.Builder()
                .imps(builder.imps)
                .clicks(builder.clicks)
                .postClickConv(builder.postClickConv)
                .postImpConv(builder.postImpConv)
                .isShowPostClickConv(builder.isShowPostClickConv)
                .isShowPostImpConv(builder.isShowPostImpConv)
                .build();

    }

    public long getImps() {
        return impClick.getImps();
    }

    public long getClicks() {
        return impClick.getClicks();
    }

    public double getCtr() {
        return impClick.getCtr();
    }

    public double getPostClickConvCr() {
        return impClick.getPostClickConvCr();
    }

    public double getPostImpConvCr() {
        return impClick.getPostImpConvCr();
    }

    public long getPostImpConv() {
        return impClick.getPostImpConv();
    }

    public boolean isShowPostImpConv(){
        return impClick.isShowPostImpConv();
    }

    public boolean isShowPostClickConv(){
        return impClick.isShowPostClickConv();
    }

    public long getPostClickConv() {
        return impClick.getPostClickConv();
    }

}
