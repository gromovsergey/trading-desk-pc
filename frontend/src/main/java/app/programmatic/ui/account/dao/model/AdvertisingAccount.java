package app.programmatic.ui.account.dao.model;

import java.math.BigDecimal;

public class AdvertisingAccount extends Account {
    private Long agencyId;
    private String name;
    private String currencyCode;
    private int currencyAccuracy;
    private String timeZone;
    private BigDecimal prepaidAmount;
    private BigDecimal commission;
    private BigDecimal selfServiceCommission;
    private boolean isGrossFlag;
    private boolean isFinancialFieldsFlag;
    private boolean isSelfServiceFlag;
    private boolean isVatEnabledFlag;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrencyAccuracy() {
        return currencyAccuracy;
    }

    public void setCurrencyAccuracy(int currencyAccuracy) {
        this.currencyAccuracy = currencyAccuracy;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public BigDecimal getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(BigDecimal prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getSelfServiceCommission() {
        return selfServiceCommission;
    }

    public void setSelfServiceCommission(BigDecimal selfServiceCommission) {
        this.selfServiceCommission = selfServiceCommission;
    }

    public boolean isGrossFlag() {
        return isGrossFlag;
    }

    public void setGrossFlag(boolean isGrossFlag) {
        this.isGrossFlag = isGrossFlag;
    }

    public boolean isFinancialFieldsFlag() {
        return isFinancialFieldsFlag;
    }

    public void setFinancialFieldsFlag(boolean financialFieldsFlag) {
        isFinancialFieldsFlag = financialFieldsFlag;
    }

    public boolean isSelfServiceFlag() {
        return isSelfServiceFlag;
    }

    public void setSelfServiceFlag(boolean selfServiceFlag) {
        isSelfServiceFlag = selfServiceFlag;
    }

    public boolean isVatEnabledFlag() {
        return isVatEnabledFlag;
    }

    public void setVatEnabledFlag(boolean vatEnabledFlag) {
        isVatEnabledFlag = vatEnabledFlag;
    }
}
