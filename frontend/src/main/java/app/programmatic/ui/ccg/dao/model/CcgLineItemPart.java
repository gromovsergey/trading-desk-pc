package app.programmatic.ui.ccg.dao.model;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class CcgLineItemPart {
    private Long ccgId;
    private String name;
    private BigDecimal budget;
    private Timestamp version;
    private Long ccgChannelId;
    private CcgDisplayStatus displayStatus;
    private Long accountId;

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public Long getCcgChannelId() {
        return ccgChannelId;
    }

    public void setCcgChannelId(Long ccgChannelId) {
        this.ccgChannelId = ccgChannelId;
    }

    public CcgDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(CcgDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
