package com.foros.session.admin.accountType;

import java.io.Serializable;

public class AccountTypeDisabledFieldsTO implements Serializable, AccountTypeDisabledFields {
    private static final boolean DEFAULT = false;

    private boolean wdTagsFlagDisabled = DEFAULT;
    private boolean publisherInventoryEstimationFlagDisabled = DEFAULT;
    private boolean siteTargetingFlagDisabled = DEFAULT;
    private boolean allowTextAdvertisingFlagDisabled = DEFAULT;
    private boolean advExclusionSiteFlagDisabled = DEFAULT;
    private boolean advExclusionSiteTagFlagDisabled = DEFAULT;
    private boolean freqCapsFlagDisabled = DEFAULT;
    private boolean displayCPMDisabled = DEFAULT;
    private boolean displayCPCDisabled = DEFAULT;
    private boolean displayCPADisabled = DEFAULT;
    private boolean displayCreativesExist = DEFAULT;
    private boolean displayCampaignsExist = DEFAULT;
    private boolean textCampaignsExist = DEFAULT;
    private boolean textCPMDisabled = DEFAULT;
    private boolean textCPCDisabled = DEFAULT;
    private boolean textCPADisabled = DEFAULT;
    private boolean allowTextKeywordAdvertisingFlagDisabled = DEFAULT;
    private boolean agencyFinancialFieldsFlagDisabled = DEFAULT;
    private boolean perCampaignInvoicingFlagDisabled = DEFAULT;
    private boolean inputRatesAndAmountsFlagDisabled = DEFAULT;
    private boolean invoiceCommissionFlagDisabled = DEFAULT;
    private boolean allowAdvExclusionApprovalDisabled = DEFAULT;
    private boolean ioManagementDisabled = DEFAULT;
    private boolean billingModelDisabled = DEFAULT;

    public AccountTypeDisabledFieldsTO() {
    }

    /**
     * Copy ctor
     * @param from source
     */
    public AccountTypeDisabledFieldsTO(AccountTypeDisabledFields from) {
        wdTagsFlagDisabled = from.isWdTagsFlagDisabled();
        publisherInventoryEstimationFlagDisabled = from.isPublisherInventoryEstimationFlagDisabled();
        siteTargetingFlagDisabled = from.isSiteTargetingFlagDisabled();
        allowTextAdvertisingFlagDisabled = from.isAllowTextAdvertisingFlagDisabled();
        advExclusionSiteFlagDisabled = from.isAdvExclusionSiteFlagDisabled();
        advExclusionSiteTagFlagDisabled = from.isAdvExclusionSiteTagFlagDisabled();
        freqCapsFlagDisabled = from.isFreqCapsFlagDisabled();
        displayCPMDisabled = from.isDisplayCPMDisabled();
        displayCPCDisabled = from.isDisplayCPCDisabled();
        displayCPADisabled = from.isDisplayCPADisabled();
        displayCreativesExist = from.isDisplayCreativesExist();
        displayCampaignsExist = from.isDisplayCampaignsExist();
        textCampaignsExist = from.isTextCampaignsExist();
        textCPMDisabled = from.isTextCPMDisabled();
        textCPCDisabled = from.isTextCPCDisabled();
        textCPADisabled = from.isTextCPADisabled();
        allowTextKeywordAdvertisingFlagDisabled = from.isAllowTextKeywordAdvertisingFlagDisabled();
        agencyFinancialFieldsFlagDisabled = from.isAgencyFinancialFieldsFlagDisabled();
        perCampaignInvoicingFlagDisabled = from.isPerCampaignInvoicingFlagDisabled();
        inputRatesAndAmountsFlagDisabled = from.isInputRatesAndAmountsFlagDisabled();
        invoiceCommissionFlagDisabled = from.isInvoiceCommissionFlagDisabled();
        allowAdvExclusionApprovalDisabled = from.isAllowAdvExclusionApprovalDisabled();
        ioManagementDisabled = from.isIOManagementDisabled();
        billingModelDisabled = from.isBillingModelFlagDisabled();
    }

    @Override
    public boolean isPublisherInventoryEstimationFlagDisabled() {
        return publisherInventoryEstimationFlagDisabled;
    }

    public void setPublisherInventoryEstimationFlagDisabled(boolean publisherInventoryEstimationFlagDisabled) {
        this.publisherInventoryEstimationFlagDisabled = publisherInventoryEstimationFlagDisabled;
    }

    @Override
    public boolean isAllowTextAdvertisingFlagDisabled() {
        return allowTextAdvertisingFlagDisabled;
    }

    public void setAllowTextAdvertisingFlagDisabled(boolean allowTextAdvertisingFlagDisabled) {
        this.allowTextAdvertisingFlagDisabled = allowTextAdvertisingFlagDisabled;
    }

    @Override
    public boolean isAdvExclusionSiteFlagDisabled() {
        return advExclusionSiteFlagDisabled;
    }

    public void setAdvExclusionSiteFlagDisabled(boolean advExclusionSiteFlagDisabled) {
        this.advExclusionSiteFlagDisabled = advExclusionSiteFlagDisabled;
    }

    @Override
    public boolean isAdvExclusionSiteTagFlagDisabled() {
        return advExclusionSiteTagFlagDisabled;
    }

    public void setAdvExclusionSiteTagFlagDisabled(boolean advExclusionSiteTagFlagDisabled) {
        this.advExclusionSiteTagFlagDisabled = advExclusionSiteTagFlagDisabled;
    }

    @Override
    public boolean isDisplayCPADisabled() {
        return displayCPADisabled;
    }

    public void setDisplayCPADisabled(boolean displayCPADisabled) {
        this.displayCPADisabled = displayCPADisabled;
    }

    @Override
    public boolean isDisplayCPCDisabled() {
        return displayCPCDisabled;
    }

    public void setDisplayCPCDisabled(boolean displayCPCDisabled) {
        this.displayCPCDisabled = displayCPCDisabled;
    }

    @Override
    public boolean isDisplayCPMDisabled() {
        return displayCPMDisabled;
    }

    public void setDisplayCPMDisabled(boolean displayCPMDisabled) {
        this.displayCPMDisabled = displayCPMDisabled;
    }

    @Override
    public boolean isDisplayCreativesExist() {
        return displayCreativesExist;
    }

    public void setDisplayCreativesExist(boolean displayCreativesExist) {
        this.displayCreativesExist = displayCreativesExist;
    }

    @Override
    public boolean isDisplayCampaignsExist() {
        return displayCampaignsExist;
    }

    public void setDisplayCampaignsExist(boolean displayCampaignsExist) {
        this.displayCampaignsExist = displayCampaignsExist;
    }

    @Override
    public boolean isTextCampaignsExist() {
        return textCampaignsExist;
    }

    public void setTextCampaignsExist(boolean textCampaignsExist) {
        this.textCampaignsExist = textCampaignsExist;
    }

    @Override
    public boolean isFreqCapsFlagDisabled() {
        return freqCapsFlagDisabled;
    }

    public void setFreqCapsFlagDisabled(boolean freqCapsFlagDisabled) {
        this.freqCapsFlagDisabled = freqCapsFlagDisabled;
    }

    @Override
    public boolean isAllowTextKeywordAdvertisingFlagDisabled() {
        return allowTextKeywordAdvertisingFlagDisabled;
    }

    public void setAllowTextKeywordAdvertisingFlagDisabled(boolean allowTextKeywordAdvertisingFlagDisabled) {
        this.allowTextKeywordAdvertisingFlagDisabled = allowTextKeywordAdvertisingFlagDisabled;
    }

    @Override
    public boolean isSiteTargetingFlagDisabled() {
        return siteTargetingFlagDisabled;
    }

    public void setSiteTargetingFlagDisabled(boolean siteTargetingFlagDisabled) {
        this.siteTargetingFlagDisabled = siteTargetingFlagDisabled;
    }

    @Override
    public boolean isTextCPADisabled() {
        return textCPADisabled;
    }

    public void setTextCPADisabled(boolean textCPADisabled) {
        this.textCPADisabled = textCPADisabled;
    }

    @Override
    public boolean isTextCPCDisabled() {
        return textCPCDisabled;
    }

    public void setTextCPCDisabled(boolean textCPCDisabled) {
        this.textCPCDisabled = textCPCDisabled;
    }

    @Override
    public boolean isTextCPMDisabled() {
        return textCPMDisabled;
    }

    public void setTextCPMDisabled(boolean textCPMDisabled) {
        this.textCPMDisabled = textCPMDisabled;
    }

    @Override
    public boolean isWdTagsFlagDisabled() {
        return wdTagsFlagDisabled;
    }

    public void setWdTagsFlagDisabled(boolean wdTagsFlagDisabled) {
        this.wdTagsFlagDisabled = wdTagsFlagDisabled;
    }

    @Override
    public boolean isAgencyFinancialFieldsFlagDisabled() {
        return agencyFinancialFieldsFlagDisabled;
    }

    public void setAgencyFinancialFieldsFlagDisabled(boolean agencyFinancialFieldsFlagDisabled) {
        this.agencyFinancialFieldsFlagDisabled = agencyFinancialFieldsFlagDisabled;
    }

    @Override
    public boolean isInputRatesAndAmountsFlagDisabled() {
        return inputRatesAndAmountsFlagDisabled;
    }

    public void setInputRatesAndAmountsFlagDisabled(boolean inputRatesAndAmountsFlagDisabled) {
        this.inputRatesAndAmountsFlagDisabled = inputRatesAndAmountsFlagDisabled;
    }

    @Override
    public boolean isInvoiceCommissionFlagDisabled() {
        return invoiceCommissionFlagDisabled;
    }

    public void setInvoiceCommissionFlagDisabled(boolean invoiceCommissionFlagDisabled) {
        this.invoiceCommissionFlagDisabled = invoiceCommissionFlagDisabled;
    }

    @Override
    public boolean isPerCampaignInvoicingFlagDisabled() {
        return perCampaignInvoicingFlagDisabled;
    }

    public void setPerCampaignInvoicingFlagDisabled(boolean perCampaignInvoicingFlagDisabled) {
        this.perCampaignInvoicingFlagDisabled = perCampaignInvoicingFlagDisabled;
    }

    @Override
    public boolean isAllowAdvExclusionApprovalDisabled() {
        return allowAdvExclusionApprovalDisabled;
    }

    @Override
    public boolean isIOManagementDisabled() {
        return ioManagementDisabled;
    }

    @Override
    public boolean isBillingModelFlagDisabled() {
        return billingModelDisabled;
    }

    public void setBillingModelFlagDisabled(boolean billingModelDisabled) {
        this.billingModelDisabled = billingModelDisabled;
    }

    public void setAllowAdvExclusionApprovalDisabled(boolean allowAdvExclusionApprovalDisabled) {
        this.allowAdvExclusionApprovalDisabled = allowAdvExclusionApprovalDisabled;
    }
}
