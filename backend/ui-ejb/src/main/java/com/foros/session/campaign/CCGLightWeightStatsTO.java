package com.foros.session.campaign;

import java.math.BigDecimal;

public class CCGLightWeightStatsTO {
    private long totalUniqueUsers;
    private BigDecimal spentBudget;
    private BigDecimal auctionCtr;
    private BigDecimal auctionEcpm;
    private long auctionsLost;
    private long selectionFailures;
    private ImpClickStatsTO impClick;

    public double getCtr() {
        return impClick.getCtr();
    }

    public double getPostClickConvCr() {
        return impClick.getPostClickConvCr();
    }

    public double getPostImpConvCr() {
        return impClick.getPostImpConvCr();
    }

    public long getImps() {
        return impClick.getImps();
    }

    public long getClicks() {
        return impClick.getClicks();
    }

    public long getPostImpConv() {
        return impClick.getPostImpConv();
    }

    public boolean isShowPostImpConv() {
        return impClick.isShowPostImpConv();
    }

    public long getPostClickConv() {
        return impClick.getPostClickConv();
    }

    public boolean isShowPostClickConv() {
        return impClick.isShowPostClickConv();
    }

    public CCGLightWeightStatsTO(ImpClickStatsTO.Builder builder, long totalUniqueUsers, BigDecimal spentBudget,
            BigDecimal auctionCtr, BigDecimal auctionEcpm, long auctionsLost, long selectionFailures) {
        this.impClick = builder.build();
        this.totalUniqueUsers = totalUniqueUsers;
        this.spentBudget = spentBudget;
        this.auctionCtr = auctionCtr;
        this.auctionEcpm = auctionEcpm;
        this.auctionsLost = auctionsLost;
        this.selectionFailures = selectionFailures;
    }

    public long getTotalUniqueUsers() {
        return totalUniqueUsers;
    }

    public BigDecimal getSpentBudget() {
        return spentBudget;
    }

    public BigDecimal getAuctionCtr() {
        return auctionCtr;
    }

    public void setAuctionCtr(BigDecimal auctionCtr) {
        this.auctionCtr = auctionCtr;
    }

    public BigDecimal getAuctionEcpm() {
        return auctionEcpm;
    }

    public long getAuctionsLost() {
        return auctionsLost;
    }

    public long getSelectionFailures() {
        return selectionFailures;
    }
}
