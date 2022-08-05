package com.foros.session.campaign;

public class ImpClickStatsTO {

    private long imps;
    private long clicks;
    private long postImpConv;
    private boolean isShowPostImpConv;
    private long postClickConv;
    private boolean isShowPostClickConv;


    public double getCtr() {
        return imps == 0 ? 0 : ((double) clicks * 100) / imps;
    }

    public double getPostClickConvCr() {
        return clicks == 0 ? 0 : ((double) postClickConv * 100) / clicks;
    }

    public double getPostImpConvCr() {
        return imps == 0 ? 0 : ((double) postImpConv * 100) / imps;
    }

    public long getImps() {
        return imps;
    }

    public long getClicks() {
        return clicks;
    }

    public long getPostImpConv() {
        return postImpConv;
    }

    public boolean isShowPostImpConv() {
        return isShowPostImpConv;
    }

    public long getPostClickConv() {
        return postClickConv;
    }

    public boolean isShowPostClickConv() {
        return isShowPostClickConv;
    }

    public static class Builder {
        private long imps;
        private long clicks;
        private long postImpConv;
        private boolean isShowPostImpConv;
        private long postClickConv;
        private boolean isShowPostClickConv;

        public Builder imps(long imps) {
            this.imps = imps;
            return this;
        }

        public Builder clicks(long totalClicks) {
            this.clicks = totalClicks;
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

        public ImpClickStatsTO build() {
            return new ImpClickStatsTO(this);
        }
    }

    private ImpClickStatsTO(Builder builder) {
        this.imps = builder.imps;
        this.clicks = builder.clicks;
        this.postImpConv = builder.postImpConv;
        this.isShowPostImpConv = builder.isShowPostImpConv;
        this.postClickConv = builder.postClickConv;
        this.isShowPostClickConv = builder.isShowPostClickConv;
    }
}
