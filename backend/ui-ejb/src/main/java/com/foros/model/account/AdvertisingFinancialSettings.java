package com.foros.model.account;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.security.BillingFrequency;
import com.foros.util.FlagsUtil;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.ValuesConstraint;
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@DiscriminatorValue("A")
@NamedQueries({
        @NamedQuery(name = "AdvertisingFinancialSettings.findByAccountId", query =
           "SELECT f FROM AdvertisingFinancialSettings f WHERE f.account.id = :accountId")
})
public class AdvertisingFinancialSettings extends AccountFinancialSettings {
    public static final long PAYMENT_TYPE = 0x1;
    public static final long INVOICE_GENERATION = 0x2;

    @RequiredConstraint
    @RangeConstraint(min = "0", max = "1000000")
    @Column(name = "MIN_INVOICE")
    private BigDecimal minInvoice = BigDecimal.valueOf(25);

    @ValuesConstraint(values = {"M", "W", "B"})
    @Column(name = "BILLING_FREQUENCY")
    private Character billingFrequency = 'M';

    @RequiredConstraint
    @Column(name = "BILLING_FREQUENCY_OFFSET")
    private Long billingFrequencyOffset = 2l;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToOne(targetEntity = AdvertisingFinancialData.class)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private AdvertisingFinancialData data;

    @RangeConstraint(min = "0", max = "4999999999")
    @Column(name = "CREDIT_LIMIT")
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "ON_ACCOUNT_CREDIT_UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date onAccountCreditUpdated;

    @Column(name = "FLAGS", nullable = false)
    private long flags;

    @Column(name="IS_FROZEN", nullable = false)
    private boolean frozen;

    @Column(name = "PAYMENT_TERMS")
    private String paymentTerms;

    public AdvertisingFinancialSettings() {
        setData(new AdvertisingFinancialData());
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
        getData().setAccountId(id);
    }

    public BigDecimal getMinInvoice() {
        return minInvoice;
    }

    public void setMinInvoice(BigDecimal minInvoice) {
        this.minInvoice = minInvoice;
        this.registerChange("minInvoice");
    }

    public void setBillingFrequency(BillingFrequency billingFrequency) {
        this.billingFrequency = billingFrequency == null ? null : billingFrequency.getLetter();
        this.registerChange("billingFrequency");
    }

    public BillingFrequency getBillingFrequency() {
        if (billingFrequency != null) {
            return BillingFrequency.valueOf(billingFrequency);
        }
        return null;
    }

    public Long getBillingFrequencyOffset() {
        return billingFrequencyOffset;
    }

    public void setBillingFrequencyOffset(Long billingFrequencyOffset) {
        this.billingFrequencyOffset = billingFrequencyOffset;
        this.registerChange("billingFrequencyOffset");
    }

    @Override
    public AdvertisingAccountBase getAccount() {
        return (AdvertisingAccountBase) super.getAccount();
    }

    public void setAccount(AdvertisingAccountBase account) {
        super.setAccount(account);
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
        this.registerChange("creditLimit");
    }

    public Date getOnAccountCreditUpdated() {
        return onAccountCreditUpdated;
    }

    public void setOnAccountCreditUpdated(Date onAccountCreditUpdated) {
        this.onAccountCreditUpdated = onAccountCreditUpdated;
        this.registerChange("onAccountCreditUpdated");
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        this.registerChange("frozen");
    }

    // Do not use it! Only for EntityUtils
    public long getFlags() {
        return flags;
    }

    // Do not use it! Only for EntityUtils
    public void setFlags(long flags) {
        this.flags = flags;
        registerChange("flags");
    }

    public boolean isPrepayPaymentType() {
        return getPaymentType() == PaymentOrderType.PREPAY;
    }

    public PaymentOrderType getPaymentType() {
        return getFlag(PAYMENT_TYPE) ? PaymentOrderType.POSTPAY : PaymentOrderType.PREPAY;
    }

    public void setPaymentType(PaymentOrderType paymentType) {
        setFlag(PAYMENT_TYPE, paymentType == PaymentOrderType.POSTPAY);
        registerChange("paymentType");
    }

    public InvoiceGenerationType getInvoiceGenerationType() {
        return getFlag(INVOICE_GENERATION) ? InvoiceGenerationType.ON_CAMPAIGN_COMPLETION : InvoiceGenerationType.FIXED_INTERVALS;
    }

    public void setInvoiceGenerationType(InvoiceGenerationType type) {
        setFlag(INVOICE_GENERATION, type == InvoiceGenerationType.ON_CAMPAIGN_COMPLETION);
        registerChange("invoiceGenerationType");
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
        this.registerChange("paymentTerms");
    }

    public AdvertisingFinancialData getData() {
        return data;
    }

    public void setData(AdvertisingFinancialData data) {
        this.data = data;
    }

    private boolean getFlag(long mask) {
        return FlagsUtil.get(flags, mask);
    }

    private void setFlag(long mask, boolean flag) {
        setFlags(FlagsUtil.set(flags, mask, flag));
    }
}
