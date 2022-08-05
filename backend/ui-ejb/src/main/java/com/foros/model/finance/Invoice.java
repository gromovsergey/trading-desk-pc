package com.foros.model.finance;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.security.AccountAddress;
import com.foros.model.security.OwnedEntity;
import com.foros.model.security.User;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import javax.annotation.security.DenyAll;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "BILLING.INVOICE")
public class Invoice extends VersionEntityBase implements OwnedEntity<AdvertiserAccount>, Serializable, Identifiable {
    @SequenceGenerator(name = "InvoiceGen", sequenceName = "INVOICE_INVOICE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InvoiceGen")
    @Column(name = "INVOICE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @Column(name = "INVOICE_LEGAL_NUMBER")
    @RequiredConstraint
    @ByteLengthConstraint(length = 20)
    private String invoiceLegalNumber;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Campaign campaign;

    @Column(name = "SOLD_TO_USER_EMAIL")
    private String soldToUserEmail;

    @Column(name = "LINE_COUNT", nullable = false)
    private int lineCount;

    @Column(name = "INVOICE_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    @Column(name = "INVOICE_EMAIL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoiceEmailDate;

    @RequiredConstraint
    @Column(name = "DUE_DATE")
    @Temporal(TemporalType.DATE)
    private Date dueDate;

    @Column(name = "CLOSED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedDate;

    @Column(name = "TOTAL_AMOUNT_NET", nullable = false)
    private BigDecimal totalAmountNet;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private BigDecimal totalAmount;

    @RequiredConstraint
    @Column(name = "TOTAL_AMOUNT_DUE", nullable = false)
    private BigDecimal totalAmountDue;

    @Column(name = "PUB_AMOUNT_NET", nullable = false)
    private BigDecimal publisherAmountNet;

    @Column(name = "OPEN_AMOUNT")
    private BigDecimal openAmount;

    @Column(name = "OPEN_AMOUNT_NET")
    private BigDecimal openAmountNet;

    @RequiredConstraint
    @Column(name = "PAID_AMOUNT")
    private BigDecimal paidAmount;

    @RequiredConstraint
    @Column(name = "CREDIT_SETTLEMENT")
    private BigDecimal creditSettlement;

    @RequiredConstraint
    @Column(name = "DEDUCT_FROM_PREPAID_AMOUNT")
    private BigDecimal deductFromPrepaidAmount;

    @Column(name = "COMM_AMOUNT")
    private BigDecimal commissionAmount;

    @JoinColumn(name = "SOLD_TO_USER_ADDRESS_ID", referencedColumnName = "ADDRESS_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private AccountAddress soldToUserAddress;

    @JoinColumn(name = "SOLD_TO_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private User soldToUser;

    @JoinColumn(name = "BILL_TO_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private User billToUser;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoice")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<InvoiceData> invoiceDatas = new LinkedHashSet<InvoiceData>();

    @Column(name = "STATUS", nullable = false)
    private char status = 'O';

    @Column(name = "ADVERTISER_NAME")
    private String advertiserName;

    @Column(name = "CAMPAIGN_NAME")
    private String campaignName;

    @JoinColumn(name = "ADV_ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", nullable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private AdvertiserAccount advertiserAccount;

    public Invoice() {
    }

    public Invoice(Long id) {
        this.id = id;
    }

    public Invoice(Long id, char status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @DenyAll
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getInvoiceLegalNumber() {
        return this.invoiceLegalNumber;
    }

    @DenyAll
    public void setInvoiceLegalNumber(String invoiceLegalNumber) {
        this.invoiceLegalNumber = invoiceLegalNumber;
        this.registerChange("invoiceLegalNumber");
    }

    public String getSoldToUserEmail() {
        return this.soldToUserEmail;
    }

    @DenyAll
    public void setSoldToUserEmail(String soldToUserEmail) {
        this.soldToUserEmail = soldToUserEmail;
        this.registerChange("soldToUserEmail");
    }

    public int getLineCount() {
        return this.lineCount;
    }

    @DenyAll
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
        this.registerChange("lineCount");
    }

    public Date getInvoiceDate() {
        return this.invoiceDate;
    }

    @DenyAll
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
        this.registerChange("invoiceDate");
    }

    public Date getInvoiceEmailDate() {
        return this.invoiceEmailDate;
    }

    @DenyAll
    public void setInvoiceEmailDate(Date invoiceEmailDate) {
        this.invoiceEmailDate = invoiceEmailDate;
        this.registerChange("invoiceEmailDate");
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    @DenyAll
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        this.registerChange("dueDate");
    }

    public Date getClosedDate() {
        return this.closedDate;
    }

    @DenyAll
    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
        this.registerChange("closedDate");
    }

    public BigDecimal getTotalAmountNet() {
        return this.totalAmountNet;
    }

    @DenyAll
    public void setTotalAmountNet(BigDecimal totalAmountNet) {
        this.totalAmountNet = totalAmountNet;
        this.registerChange("totalAmountNet");
    }

    public BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    @DenyAll
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.registerChange("totalAmount");
    }

    public BigDecimal getPublisherAmountNet() {
        return this.publisherAmountNet;
    }

    @DenyAll
    public void setPublisherAmountNet(BigDecimal publisherAmountNet) {
        this.publisherAmountNet = publisherAmountNet;
        this.registerChange("publisherAmountNet");
    }

    public BigDecimal getOpenAmount() {
        return this.openAmount;
    }

    @DenyAll
    public void setOpenAmount(BigDecimal openAmount) {
        this.openAmount = openAmount;
        this.registerChange("openAmount");
    }

    public BigDecimal getOpenAmountNet() {
        return this.openAmountNet;
    }

    @DenyAll
    public void setOpenAmountNet(BigDecimal openAmountNet) {
        this.openAmountNet = openAmountNet;
        this.registerChange("openAmountNet");
    }

    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }

    @DenyAll
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
        this.registerChange("paidAmount");
    }

    public BigDecimal getCreditSettlement() {
        return this.creditSettlement;
    }

    @DenyAll
    public void setCreditSettlement(BigDecimal creditSettlement) {
        this.creditSettlement = creditSettlement;
        this.registerChange("creditSettlement");
    }

    public BigDecimal getDeductFromPrepaidAmount() {
        return this.deductFromPrepaidAmount;
    }

    @DenyAll
    public void setDeductFromPrepaidAmount(BigDecimal deductFromPrepaidAmount) {
        this.deductFromPrepaidAmount = deductFromPrepaidAmount;
        this.registerChange("deductFromPrepaidAmount");
    }

    public BigDecimal getCommissionAmount() {
        return this.commissionAmount;
    }

    @DenyAll
    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
        this.registerChange("commissionAmount");
    }

    public AccountAddress getSoldToUserAddress() {
        return this.soldToUserAddress;
    }

    @DenyAll
    public void setSoldToUserAddress(AccountAddress soldToUserAddress) {
        this.soldToUserAddress = soldToUserAddress;
        this.registerChange("soldToUserAddress");
    }

    public Campaign getCampaign() {
        return this.campaign;
    }

    @DenyAll
    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        this.registerChange("campaign");
    }

    public User getSoldToUser() {
        return this.soldToUser;
    }

    @DenyAll
    public void setSoldToUser(User soldToUser) {
        this.soldToUser = soldToUser;
        this.registerChange("soldToUser");
    }

    public User getBillToUser() {
        return this.billToUser;
    }

    @DenyAll
    public void setBillToUser(User billToUser) {
        this.billToUser = billToUser;
        this.registerChange("billToUser");
    }

    public Set<InvoiceData> getInvoiceDatas() {
        return new ChangesSupportSet<InvoiceData>(this, "invoiceDatas", invoiceDatas);
    }

    @DenyAll
    public void setInvoiceDatas(Set<InvoiceData> invoiceDatas) {
        this.invoiceDatas = invoiceDatas;
        this.registerChange("invoiceDatas");
    }

    public void setStatus(FinanceStatus status) {
        this.status = status.getLetter();
        this.registerChange("status");
    }

    public FinanceStatus getStatus() {
        return FinanceStatus.valueOf(status);
    }

    public String getAdvertiserName() {
        return this.advertiserName;
    }

    @DenyAll
    public void setAdvertiserName(String advertiserName) {
        this.advertiserName = advertiserName;
        this.registerChange("advertiserName");
    }

    public String getCampaignName() {
        return this.campaignName;
    }

    @DenyAll
    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
        this.registerChange("campaignName");
    }

    public BigDecimal getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(BigDecimal totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
        this.registerChange("totalAmountDue");
    }

    @Override
    public AdvertiserAccount getAccount() {
        return this.advertiserAccount;
    }

    @DenyAll
    public void setAccount(AdvertiserAccount advertiserAccount) {
        this.advertiserAccount = advertiserAccount;
        this.registerChange("advertiserAccount");
    }

    public boolean isPrintable() {
        return (getStatus() == FinanceStatus.OPEN || getStatus() == FinanceStatus.GENERATED) && hasInvoiceReport();
    }

    private boolean hasInvoiceReport() {
        return getAccount().getCountry().getInvoiceReport() != null;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Invoice)) {
            return false;
        }

        Invoice other = (Invoice)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.finance.Invoice[id=" + getId() + "]";
    }
}
