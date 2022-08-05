package app.programmatic.ui.agentreport.dao.model;

import app.programmatic.ui.common.model.RateType;
import app.programmatic.ui.common.model.VersionEntityBase;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "AGENTREPORTSTAT")
public class AgentReportStat extends VersionEntityBase<Long> {

    @Id
    @SequenceGenerator(name = "StatGen", sequenceName = "agentreportstat_stat_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "StatGen")
    @Column(name = "stat_id", nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Column(name = "status", nullable = false)
    private Character status;

    @Column(name = "fin_date", updatable = false)
    private LocalDate date;

    @Column(name = "campaign_id", updatable = false)
    private Long campaignId;

    @Column(name = "rate_type", updatable = false)
    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000000", inclusive = false)
    @Digits(integer=12, fraction=5)
    @Column(name = "rate_value", updatable = false)
    private BigDecimal rateValue;

    @Min(0)
    @Column(name = "inventory_confirmed")
    private Long inventoryAmountConfirmed;

    @Size(min = 1, max = 200)
    @Column(name = "inventory_comment")
    private String inventoryAmountComment;

    @Size(min = 1, max = 50)
    @Column(name = "invoice_number")
    private String invoiceNumber;

    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1000000000", inclusive = false)
    @Column(name = "pub_amount_confirmed")
    private BigDecimal pubAmountConfirmed;

    @Size(min = 1, max = 200)
    @Column(name = "pub_amount_comment")
    private String pubAmountComment;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public void setRateValue(BigDecimal rateValue) {
        this.rateValue = rateValue;
    }

    public Long getInventoryAmountConfirmed() {
        return inventoryAmountConfirmed;
    }

    public void setInventoryAmountConfirmed(Long inventoryAmountConfirmed) {
        this.inventoryAmountConfirmed = inventoryAmountConfirmed;
    }

    public String getInventoryAmountComment() {
        return inventoryAmountComment;
    }

    public void setInventoryAmountComment(String inventoryAmountComment) {
        this.inventoryAmountComment = inventoryAmountComment;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public BigDecimal getPubAmountConfirmed() {
        return pubAmountConfirmed;
    }

    public void setPubAmountConfirmed(BigDecimal pubAmountConfirmed) {
        this.pubAmountConfirmed = pubAmountConfirmed;
    }

    public String getPubAmountComment() {
        return pubAmountComment;
    }

    public void setPubAmountComment(String pubAmountComment) {
        this.pubAmountComment = pubAmountComment;
    }
}
