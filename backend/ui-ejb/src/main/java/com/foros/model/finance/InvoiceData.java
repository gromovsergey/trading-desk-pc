package com.foros.model.finance;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.VersionEntityBase;
import com.foros.model.Identifiable;
import com.foros.model.campaign.CampaignCreativeGroup;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "BILLING.INVOICEDATA")
@NamedQueries(
      {
    @NamedQuery(name = "InvoiceData.findByInvoiceId", query = "SELECT i FROM InvoiceData i WHERE i.invoice.id = :id")
  })
public class InvoiceData extends VersionEntityBase implements Serializable, Identifiable {
    
    @Transient
    private BigDecimal amount;

    @Column(name = "AMOUNT_NET", updatable = false)
    private BigDecimal amountNet;

    @Column(name = "COMM_AMOUNT", updatable = false)
    private BigDecimal commAmount;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    
    @Column(name = "UNIT_OF_MEASURE", nullable = false)
    private String unitOfMeasure;
    
    @Column(name = "UNIT_PRICE", nullable = false)
    private BigDecimal unitPrice;
    
    @Column(name = "QUANTITY", nullable = false)
    private long quantity;

    @Column(name = "QUANTITY_CREDITED", updatable = false)
    private long quantityCredited;
    
    @SequenceGenerator(name = "InvoiceDataGen", sequenceName = "INVOICEDATA_INVOICE_DATA_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "InvoiceDataGen")
    @Column(name = "INVOICE_DATA_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;
    
    @JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private CampaignCreativeGroup ccg;
    
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "INVOICE_ID")
    @ManyToOne
    private Invoice invoice;
    
    @JoinColumn(name = "ORIGINAL_INVOICE_DATA_ID", referencedColumnName = "INVOICE_DATA_ID")
    @ManyToOne
    private InvoiceData originalInvoiceData;

    /** 
     * Creates a new instance of InvoiceData 
     */
    public InvoiceData() {
    }

    /**
     * Creates a new instance of InvoiceData with the specified values.
     */
    public InvoiceData(Long id) {
        this.id = id;
    }

    public BigDecimal getAmountNet() {
        return amountNet;
    }

    public void setAmountNet(BigDecimal amountNet) {
        this.amountNet = amountNet;
    }

    public BigDecimal getCommAmount() {
        return commAmount;
    }

    public void setCommAmount(BigDecimal commAmount) {
        this.commAmount = commAmount;
    }

    /**
     * Gets the description of this InvoiceData.
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of this InvoiceData to the specified value.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
        this.registerChange("description");
    }

    /**
     * Gets the unitOfMeasure of this InvoiceData.
     * @return the unitOfMeasure
     */
    public String getUnitOfMeasure() {
        return this.unitOfMeasure;
    }

    /**
     * Sets the unitOfMeasure of this InvoiceData to the specified value.
     *
     * @param unitOfMeasure the new unitOfMeasure
     */
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
        this.registerChange("unitOfMeasure");
    }

    /**
     * Gets the unitPrice of this InvoiceData.
     * @return the unitPrice
     */
    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    /**
     * Sets the unitPrice of this InvoiceData to the specified value.
     *
     * @param unitPrice the new unitPrice
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        this.registerChange("unitPrice");
    }

    /**
     * Gets the quantity of this InvoiceData.
     * @return the quantity
     */
    public long getQuantity() {
        return this.quantity;
    }

    /**
     * Sets the quantity of this InvoiceData to the specified value.
     *
     * @param quantity the new quantity
     */
    public void setQuantity(long quantity) {
        this.quantity = quantity;
        this.registerChange("quantity");
    }

    /**
     * Gets the invoiceDataId of this InvoiceData.
     * @return the invoiceDataId
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the invoiceDataId of this InvoiceData to the specified value.
     */
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * Gets the ccgId of this InvoiceData.
     * @return the ccgId
     */
    public CampaignCreativeGroup getCcg() {
        return this.ccg;
    }

    /**
     * Sets the ccgId of this InvoiceData to the specified value.
     */
    public void setCcg(CampaignCreativeGroup ccg) {
        this.ccg = ccg;
        this.registerChange("ccg");
    }

    /**
     * Gets the invoiceId of this InvoiceData.
     * @return the invoiceId
     */
    public Invoice getInvoice() {
        return this.invoice;
    }

    /**
     * Sets the invoiceId of this InvoiceData to the specified value.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
        this.registerChange("invoice");
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        
        return hash;
    }

    /**
     * Determines whether another object is equal to this InvoiceData.  The result is
     * <code>true</code> if and only if the argument is not null and is a InvoiceData object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InvoiceData)) {
            return false;
        }
        
        InvoiceData other = (InvoiceData)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        
        return true;
    }

    public InvoiceData getOriginalInvoiceData() {
        return originalInvoiceData;
    }

    public void setOriginalInvoiceData(InvoiceData originalInvoiceData) {
        this.originalInvoiceData = originalInvoiceData;
        this.registerChange("originalInvoiceData");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getQuantityCredited() {
        return quantityCredited;
    }

    public void setQuantityCredited(long quantityCredited) {
        this.quantityCredited = quantityCredited;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.finance.InvoiceData[id=" + getId() + "]";
    }
}
