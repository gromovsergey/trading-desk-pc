package app.programmatic.ui.account.dao.model;

import java.math.BigDecimal;

public class AccountStats {
    private Long id;
    private String name;
    private String currencyCode;
    private Long impressions;
    private Long clicks;
    private BigDecimal ctr;
    private BigDecimal revenue;
    private String displayStatus;
    private boolean testFlag;
    private BigDecimal creditUsed;
    private boolean isAgency;

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

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public boolean isTestFlag() {
        return testFlag;
    }

    public void setTestFlag(boolean testFlag) {
        this.testFlag = testFlag;
    }

    public boolean isAgency() {
        return isAgency;
    }

    public void setAgency(boolean agency) {
        isAgency = agency;
    }
}
