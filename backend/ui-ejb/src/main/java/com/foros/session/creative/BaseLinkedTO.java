package com.foros.session.creative;

import com.foros.session.campaign.ImpClickStatsTO;

import java.math.BigDecimal;

public abstract class BaseLinkedTO {
    private long setNumber;
    private ImpClickStatsTO impClick;
    private BigDecimal inventoryCost;
    private BigDecimal targetingCost;
    private BigDecimal creditUsed;
    private BigDecimal totalCost;
    private BigDecimal totalValue;

    public BaseLinkedTO(ImpClickStatsTO.Builder builder) {
        impClick = builder.build();
    }

    public long getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(long setNumber) {
        this.setNumber = setNumber;
    }

    public long getClicks() {
        return impClick.getClicks();
    }

    public long getImpressions() {
        return impClick.getImps();
    }

    public double getCtr() {
        return impClick.getCtr();
    }

    public BigDecimal getInventoryCost() {
        return inventoryCost;
    }

    public void setInventoryCost(BigDecimal inventoryCost) {
        this.inventoryCost = inventoryCost;
    }

    public BigDecimal getTargetingCost() {
        return targetingCost;
    }

    public void setTargetingCost(BigDecimal targetingCost) {
        this.targetingCost = targetingCost;
    }

    public BigDecimal getTotalCost() {
        if (totalCost == null) {
            totalCost = getTotalValue().subtract(creditUsed);
        }
        return totalCost;
    }

    public BigDecimal getTotalValue() {
        if (totalValue == null) {
            totalValue = inventoryCost.add(targetingCost);
        }
        return totalValue;
    }

    public double getEcpm() {
        return impClick.getImps() == 0 ? 0 : getTotalValue().doubleValue() * 1000 / impClick.getImps();
    }

    public double getAverageActualCPC() {
        return impClick.getClicks() == 0 ? 0 : getTotalValue().doubleValue() / impClick.getClicks();
    }

    public BigDecimal getCreditUsed() {
        return creditUsed;
    }

    public void setCreditUsed(BigDecimal creditUsed) {
        this.creditUsed = creditUsed;
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