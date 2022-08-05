package app.programmatic.ui.flight.dao.model.stat;

import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.math.BigDecimal;

public class FlightBaseStat {
    private Long id;
    private String name;
    private MajorDisplayStatus displayStatus;
    private Long requests;
    private Long impressions;
    private Long clicks;
    private BigDecimal ctr;
    private BigDecimal ecpm;
    private BigDecimal totalCost;
    private BigDecimal budget;
    private BigDecimal spentBudget;
    private Long postImpConv;
    private Long postClickConv;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Long getRequests() {
        return requests;
    }

    public void setRequests(Long requests) {
        this.requests = requests;
    }

    public Long getImpressions() {
        return impressions;
    }

    public void setImpressions(Long impressions) {
        this.impressions = impressions;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
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

    public void setEcpm(BigDecimal ecpm) {
        this.ecpm = ecpm;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public BigDecimal getSpentBudget() {
        return spentBudget;
    }

    public void setSpentBudget(BigDecimal spentBudget) {
        this.spentBudget = spentBudget;
    }

    public Long getPostImpConv() {
        return postImpConv;
    }

    public void setPostImpConv(Long postImpConv) {
        this.postImpConv = postImpConv;
    }

    public Long getPostClickConv() {
        return postClickConv;
    }

    public void setPostClickConv(Long postClickConv) {
        this.postClickConv = postClickConv;
    }
}
