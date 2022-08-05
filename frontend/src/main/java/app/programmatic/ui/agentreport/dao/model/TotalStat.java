package app.programmatic.ui.agentreport.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import app.programmatic.ui.agentreport.tool.serialization.MoneySerializer;

import java.math.BigDecimal;

public class TotalStat {
    private int month;
    private int year;
    private AgentReportStatus status;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal invoiceAmount;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal publisherAmount;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal agencyAmount;

    @JsonSerialize(using = MoneySerializer.class)
    private BigDecimal principalAmount;

    public TotalStat() {
    }

    public TotalStat(int month, int year, AgentReportStatus status) {
        this.month = month;
        this.year = year;
        this.status = status;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public AgentReportStatus getStatus() {
        return status;
    }

    public void setStatus(AgentReportStatus status) {
        this.status = status;
    }

    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getPublisherAmount() {
        return publisherAmount;
    }

    public void setPublisherAmount(BigDecimal publisherAmount) {
        this.publisherAmount = publisherAmount;
    }

    public BigDecimal getAgencyAmount() {
        return agencyAmount;
    }

    public void setAgencyAmount(BigDecimal agencyAmount) {
        this.agencyAmount = agencyAmount;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
}
