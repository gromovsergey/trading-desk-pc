package app.programmatic.ui.agentreport.dao.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import app.programmatic.ui.agentreport.tool.serialization.MoneyDeserializer;
import app.programmatic.ui.agentreport.tool.serialization.MoneySerializer;
import app.programmatic.ui.common.model.RateType;
import app.programmatic.ui.agentreport.tool.jaxb.BigDecimalXmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

@XmlRootElement(name = "row")
public class MonthlyStat {
    private Long id;
    private Long campaignId;
    private Long version;

    private String advertiserName;
    private String contractNumber;
    private String clientName;
    private String campaignName;
    private String invoiceNumber;

    private RateType rateType;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal rateValue;

    private AgentReportStatus status;

    private Long inventoryAmount;
    private Long inventoryAmountConfirmed;
    private String inventoryAmountComment;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal totalAmount;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal totalAmountConfirmed;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal pubAmount;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal pubAmountConfirmed;

    private String pubAmountComment;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal prepaymentAmount;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal totalNetAmount;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal agentAmount;

    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    private BigDecimal principalAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @XmlElement
    public String getAdvertiserName() {
        return advertiserName;
    }

    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
    }

    @XmlElement
    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    @XmlElement
    public String getInvoiceNumber() {
        if (invoiceNumber != null && invoiceNumber.isEmpty()) {
            return null;
        }
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public AgentReportStatus getStatus() {
        return status;
    }

    public void setStatus(AgentReportStatus status) {
        this.status = status;
    }

    public Long getInventoryAmount() {
        return inventoryAmount;
    }

    public void setInventoryAmount(Long inventoryAmount) {
        this.inventoryAmount = inventoryAmount;
    }

    public Long getInventoryAmountConfirmed() {
        return inventoryAmountConfirmed;
    }

    public void setInventoryAmountConfirmed(Long inventoryAmountConfirmed) {
        this.inventoryAmountConfirmed = inventoryAmountConfirmed;
    }

    public String getInventoryAmountComment() {
        if (inventoryAmountComment != null && inventoryAmountComment.isEmpty()) {
            return null;
        }
        return inventoryAmountComment;
    }

    public void setInventoryAmountComment(String inventoryAmountComment) {
        this.inventoryAmountComment = inventoryAmountComment;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getTotalAmountConfirmed() {
        return totalAmountConfirmed;
    }

    public void setTotalAmountConfirmed(BigDecimal totalAmountConfirmed) {
        this.totalAmountConfirmed = totalAmountConfirmed;
    }

    public BigDecimal getPubAmount() {
        return pubAmount;
    }

    public void setPubAmount(BigDecimal pubAmount) {
        this.pubAmount = pubAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPubAmountConfirmed() {
        return pubAmountConfirmed;
    }

    public void setPubAmountConfirmed(BigDecimal pubAmountConfirmed) {
        this.pubAmountConfirmed = pubAmountConfirmed;
    }

    public String getPubAmountComment() {
        if (pubAmountComment != null && pubAmountComment.isEmpty()) {
            return null;
        }
        return pubAmountComment;
    }

    public void setPubAmountComment(String pubAmountComment) {
        this.pubAmountComment = pubAmountComment;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(BigDecimal prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getTotalNetAmount() {
        return totalNetAmount;
    }

    public void setTotalNetAmount(BigDecimal totalNetAmount) {
        this.totalNetAmount = totalNetAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getAgentAmount() {
        return agentAmount;
    }

    public void setAgentAmount(BigDecimal agentAmount) {
        this.agentAmount = agentAmount;
    }

    @XmlJavaTypeAdapter(BigDecimalXmlAdapter.class)
    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }
}
