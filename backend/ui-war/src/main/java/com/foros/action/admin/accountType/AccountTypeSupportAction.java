package com.foros.action.admin.accountType;

import com.foros.action.BaseActionSupport;
import com.foros.action.Invalidable;
import com.foros.model.Status;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.security.AdvExclusionsApprovalType;
import com.foros.model.template.Template;
import com.foros.model.template.TemplateTO;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.security.AccountRole;
import com.foros.session.EntityTO;
import com.foros.session.admin.accountType.AccountTypeDisabledFields;
import com.foros.session.admin.accountType.AccountTypeService;
import com.foros.session.campaign.CCGEntityTO;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.creative.CreativeSizeTO;
import com.foros.session.LocalizableNameEntityComparator;
import com.foros.session.template.TemplateService;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.IdNameComparator;
import com.foros.util.comparator.LocalizableTOComparator;
import com.foros.util.tree.TreeNode;

import com.opensymphony.xwork2.ModelDriven;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

public class AccountTypeSupportAction extends BaseActionSupport implements ModelDriven<AccountType>, Invalidable {

    protected DeviceTargetingHelper deviceHelper;

    @EJB
    protected DeviceChannelService deviceChannelService;

    @EJB
    protected AccountTypeService service;

    @EJB
    protected CreativeSizeService sizeSvc;

    @EJB
    protected TemplateService templateSvc;

    protected AccountType entity = new AccountType();

    protected AccountTypeDisabledFields changesCheck;

    private Map<CheckNumber, CheckEntry> channelChecks;
    private Map<CheckNumber, CheckEntry> campaignChecks;

    public enum CheckUOM {
        hour, day;

        public String getNameKey() {
            return "form.select." + this.name();
        }
    }

    public enum CheckNumber {
        First(1) {
            @Override
            public TimeSpan getDefaultTimeSpan() {
                return new TimeSpan(1L, TimeUnit.HOUR);
            }

            ;
        },
        Second(2) {
            @Override
            public TimeSpan getDefaultTimeSpan() {
                return new TimeSpan(1L, TimeUnit.DAY);
            }

            ;
        },
        Third(3) {
            @Override
            public TimeSpan getDefaultTimeSpan() {
                return new TimeSpan(7L, TimeUnit.DAY);
            }

            ;
        };

        private int value;

        public int value() {
            return this.value;
        }

        private CheckNumber(int value) {
            this.value = value;
        }

        public abstract TimeSpan getDefaultTimeSpan();
    }

    public static class CheckEntry {
        private Long value;
        private CheckUOM uom;
        private TimeSpan timeSpan;

        public CheckEntry() {
            this.timeSpan = new TimeSpan();
        }

        public CheckEntry(TimeSpan timeSpan) {
            if (timeSpan != null) {
                if (timeSpan.getUnit() == TimeUnit.HOUR) {
                    this.value = timeSpan.getValue();
                    this.uom = CheckUOM.hour;
                    this.timeSpan = timeSpan;
                } else {
                    this.uom = CheckUOM.day;
                    this.value = TimeUnit.DAY.convertToUnits(timeSpan.getValueInSeconds());
                    this.timeSpan = timeSpan;
                    timeSpan.setValue(this.value);
                    timeSpan.setUnit(TimeUnit.DAY);
                }
            } else {
                this.value = null;
                this.uom = CheckUOM.hour;
                this.timeSpan = new TimeSpan(this.value, TimeUnit.HOUR);
            }
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.timeSpan.setValue(value);
            this.value = value;
        }

        public CheckUOM getUom() {
            return uom;
        }

        public void setUom(CheckUOM uom) {
            switch (uom) {
            case hour:
                this.timeSpan.setUnit(TimeUnit.HOUR);
                break;
            case day:
                this.timeSpan.setUnit(TimeUnit.DAY);
                break;
            default:
                this.timeSpan.setUnit(TimeUnit.DAY);
            }
            this.uom = uom;
        }

        public TimeSpan getTimeSpan() {
            return timeSpan;
        }
    }

    // radio button value holder

    // common flags used for enabled and disabled values
    private Map<String, Boolean> radioButtonFlagValues = new LinkedHashMap<>();
    // agency account role
    private Map<String, Boolean> financialFieldsValues = new LinkedHashMap<>();

    // agency and advertiser account role
    private Map<String, Boolean> ownershipValues = new LinkedHashMap<>();
    private Map<String, Boolean> invoicingValues = new LinkedHashMap<>();
    private Map<String, Boolean> inputRatesAndAmountsValues = new LinkedHashMap<>();
    private Map<String, Boolean> invoiceCommissionValues = new LinkedHashMap<>();
    private Map<String, String> auctionRateValues = new LinkedHashMap<>();
    private Map<String, Boolean> billingModelValues = new LinkedHashMap<>();
    // publisher account role
    private Map<String, Boolean> showTagsValues = new LinkedHashMap<>();

    // dependency value holders
    private List<EntityTO> frequencyCapsLinkedSites;
    private List<EntityTO> publisherInventoryLinkedTags;
    private List<EntityTO> wdTagsLinkedSites;
    private List<EntityTO> advExclusionLinkedSites;
    private List<EntityTO> advExclusionLinkedTags;
    private List<CCGEntityTO> siteTargetingLinkedCCgs;
    private List<CCGEntityTO> keywordTargetedLinkedCcgs;

    private List<CCGEntityTO> displayCpmLinkedCampaignCreativeGroups;
    private List<CCGEntityTO> displayCpaLinkedCampaignCreativeGroups;
    private List<CCGEntityTO> displayCpcLinkedCampaignCreativeGroups;
    private List<EntityTO> displayCreatives;
    private List<EntityTO> displayCampaigns;
    private List<EntityTO> textCampaigns;
    private List<CCGEntityTO> textCpmLinkedCampaignCreativeGroups;
    private List<CCGEntityTO> textCpaLinkedCampaignCreativeGroups;
    private List<CCGEntityTO> textCpcLinkedCampaignCreativeGroups;
    private List<EntityTO> linkedAccounts;

    protected List<CreativeSizeTO> creativeSizeList = new ArrayList<>();
    protected List<TemplateTO> creativeTemplateList = new ArrayList<>();
    protected List<TemplateTO> discoverTemplateList = new ArrayList<>();

    private List<CreativeSizeTO> availableSizes;
    private List<TemplateTO> availableCreativeTemplates;
    private List<TemplateTO> availableDiscoverTemplates;

    public AccountType getEntity() {
        return entity;
    }

    public void setEntity(AccountType entity) {
        this.entity = entity;
    }

    @Override
    public AccountType getModel() {
        return entity;
    }

    public List<TemplateTO> getCreativeTemplateList() {
        return creativeTemplateList;
    }

    public void setCreativeTemplateList(List<TemplateTO> creativeTemplateList) {
        this.creativeTemplateList = creativeTemplateList;
    }

    public List<TemplateTO> getDiscoverTemplateList() {
        return discoverTemplateList;
    }

    public void setDiscoverTemplateList(List<TemplateTO> discoverTemplateList) {
        this.discoverTemplateList = discoverTemplateList;
    }

    public List<CreativeSizeTO> getCreativeSizeList() {
        return creativeSizeList;
    }

    public void setCreativeSizeList(List<CreativeSizeTO> creativeSizeList) {
        this.creativeSizeList = creativeSizeList;
    }

    // required for view
    public Collection<CreativeSize> getNonTextSizes() {
        List<CreativeSize> list = new ArrayList<>(getEntity().getCreativeSizes());

        CollectionUtils.filter(list, new Filter<CreativeSize>() {
            @Override
            public boolean accept(CreativeSize size) {
                return !size.isText();
            }
        });

        Collections.sort(list, new LocalizableNameEntityComparator());

        return list;
    }

    public Collection<Template> getNonTextTemplates() {
        List<Template> list = new ArrayList<>(getEntity().getTemplates());

        CollectionUtils.filter(list, new Filter<Template>() {
            @Override
            public boolean accept(Template template) {
                return !template.isText();
            }
        });

        Collections.sort(list, new LocalizableNameEntityComparator());
        return list;
    }

    public List<CreativeSizeTO> getAvailableSizes() {
        if (availableSizes == null) {
            availableSizes = sizeSvc.findAvailableSizes(getModel().getId());
            Collections.sort(availableSizes, new LocalizableTOComparator<CreativeSizeTO>());
        }
        return availableSizes;
    }

    public List<TemplateTO> getAvailableCreativeTemplates() {
        if (availableCreativeTemplates == null) {
            availableCreativeTemplates = templateSvc.findAvailableCreativeTemplates(getModel().getId());
            Collections.sort(availableCreativeTemplates, new LocalizableTOComparator<TemplateTO>());
        }
        return availableCreativeTemplates;
    }

    public List<TemplateTO> getAvailableDiscoverTemplates() {
        if (availableDiscoverTemplates == null) {
            availableDiscoverTemplates = templateSvc.findAvailableDiscoverTemplates(getModel().getId());
            Collections.sort(availableDiscoverTemplates, new LocalizableTOComparator<TemplateTO>());
        }
        return availableDiscoverTemplates;
    }

    public List<EntityTO> getFrequencyCapsLinkedSites() {
        if (frequencyCapsLinkedSites == null) {
            frequencyCapsLinkedSites = service.getSitesLinkedByFrequencyCapsFlag(getEntity().getId());
            EntityUtils.applyStatusRules(frequencyCapsLinkedSites, null, true);
        }

        return frequencyCapsLinkedSites;
    }

    public List<EntityTO> getPublisherInventoryLinkedTags() {
        if (publisherInventoryLinkedTags == null) {
            publisherInventoryLinkedTags = service.getTagsLinkedByInventoryEstimationFlag(getEntity().getId());
            EntityUtils.applyStatusRules(publisherInventoryLinkedTags, null, true);
        }

        return publisherInventoryLinkedTags;
    }

    public List<EntityTO> getWdTagsLinkedSites() {
        if (wdTagsLinkedSites == null) {
            wdTagsLinkedSites = service.getSiteListForWDTagsFlag(getEntity().getId());
            EntityUtils.applyStatusRules(wdTagsLinkedSites, null, true);
        }

        return wdTagsLinkedSites;
    }

    public List<EntityTO> getAdvExclusionLinkedSites() {
        if (advExclusionLinkedSites == null) {
            advExclusionLinkedSites = service.getSitesLinkedByAdvExclusionFlag(getEntity().getId());
            EntityUtils.applyStatusRules(advExclusionLinkedSites, null, true);
        }

        return advExclusionLinkedSites;
    }

    public List<EntityTO> getAdvExclusionLinkedTags() {
        if (advExclusionLinkedTags == null) {
            advExclusionLinkedTags = service.getTagsLinkedByAdvExclusionFlag(getEntity().getId());
            EntityUtils.applyStatusRules(advExclusionLinkedTags, null, true);
        }

        return advExclusionLinkedTags;
    }

    public List<CCGEntityTO> getSiteTargetingLinkedCcgs() {
        if (siteTargetingLinkedCCgs == null) {
            siteTargetingLinkedCCgs = service.getCampaignCreativeGroupsLinkedByFlag(getEntity(), CampaignCreativeGroup.INCLUDE_SPECIFIC_PUBL);
            EntityUtils.applyStatusRules(siteTargetingLinkedCCgs, null, true);
        }

        return siteTargetingLinkedCCgs;
    }

    public List<CCGEntityTO> getKeywordTargetedLinkedCcgs() {
        if (keywordTargetedLinkedCcgs == null) {
            keywordTargetedLinkedCcgs = service.getLinkedTextCampaignCreativeGroups(getEntity());
            EntityUtils.applyStatusRules(keywordTargetedLinkedCcgs, null, true);
        }

        return keywordTargetedLinkedCcgs;
    }

    public List<CCGEntityTO> getDisplayCpmLinkedCampaignCreativeGroups() {
        if (displayCpmLinkedCampaignCreativeGroups == null) {
            displayCpmLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPM, CCGType.DISPLAY, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(displayCpmLinkedCampaignCreativeGroups, null, true);
        }

        return displayCpmLinkedCampaignCreativeGroups;
    }

    public List<CCGEntityTO> getDisplayCpaLinkedCampaignCreativeGroups() {
        if (displayCpaLinkedCampaignCreativeGroups == null) {
            displayCpaLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPA, CCGType.DISPLAY, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(displayCpaLinkedCampaignCreativeGroups, null, true);
        }

        return displayCpaLinkedCampaignCreativeGroups;
    }

    public List<CCGEntityTO> getDisplayCpcLinkedCampaignCreativeGroups() {
        if (displayCpcLinkedCampaignCreativeGroups == null) {
            displayCpcLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPC, CCGType.DISPLAY, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(displayCpcLinkedCampaignCreativeGroups, null, true);
        }

        return displayCpcLinkedCampaignCreativeGroups;
    }

    public List<EntityTO> getDisplayCreatives() {
        if (displayCreatives == null) {
            displayCreatives = service.getDisplayCreativesLinkedToAccountType(getEntity());
            EntityUtils.applyStatusRules(displayCreatives, null, true);
        }

        return displayCreatives;
    }

    public List<EntityTO> getDisplayCampaigns() {
        if (displayCampaigns == null) {
            displayCampaigns = service.getDisplayCampaignsLinkedToAccountType(getEntity());
            EntityUtils.applyStatusRules(displayCampaigns, null, true);
        }

        return displayCampaigns;
    }

    public List<EntityTO> getTextCampaigns() {
        if (textCampaigns == null) {
            textCampaigns = service.getTextCampaignsLinkedToAccountType(getEntity());
            EntityUtils.applyStatusRules(textCampaigns, null, true);
        }

        return textCampaigns;
    }

    public List<CCGEntityTO> getTextCpmLinkedCampaignCreativeGroups() {
        if (textCpmLinkedCampaignCreativeGroups == null) {
            textCpmLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPM, CCGType.TEXT, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(textCpmLinkedCampaignCreativeGroups, null, true);
        }

        return textCpmLinkedCampaignCreativeGroups;
    }

    public List<CCGEntityTO> getTextCpaLinkedCampaignCreativeGroups() {
        if (textCpaLinkedCampaignCreativeGroups == null) {
            textCpaLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPA, CCGType.TEXT, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(textCpaLinkedCampaignCreativeGroups, null, true);
        }

        return textCpaLinkedCampaignCreativeGroups;
    }

    public List<CCGEntityTO> getTextCpcLinkedCampaignCreativeGroups() {
        if (textCpcLinkedCampaignCreativeGroups == null) {
            textCpcLinkedCampaignCreativeGroups = service.getCCGRateTypeListLinkedToAccountType(getEntity(),
                RateType.CPC, CCGType.TEXT, TGTType.CHANNEL);
            EntityUtils.applyStatusRules(textCpcLinkedCampaignCreativeGroups, null, true);
        }

        return textCpcLinkedCampaignCreativeGroups;
    }

    public List<EntityTO> getFinancialFieldsLinkedAccunts() {
        return getLinkedAccounts();
    }

    public List<EntityTO> getInvoiceCommissionLinkedAccunts() {
        return getLinkedAccounts();
    }

    public List<EntityTO> getInputRatesAndAmountsLinkedAccunts() {
        return getLinkedAccounts();
    }

    public List<EntityTO> getInvoicingLinkedAccunts() {
        return getLinkedAccounts();
    }

    private List<EntityTO> getLinkedAccounts() {
        if (linkedAccounts == null && getEntity().getId() != null) {
            linkedAccounts = service.getAccountLinkedByAccountType(getEntity());
            EntityUtils.applyStatusRules(linkedAccounts, null, true);
        }
        return linkedAccounts;
    }

    public AccountRole[] getAvailableAccountRoles() {
        return AccountRole.values();
    }

    private void populateRadioControls() {
        radioButtonFlagValues.put("AccountType.enabled", true);
        radioButtonFlagValues.put("AccountType.disabled", false);

        ownershipValues.put("AccountType.ownership.company", true);
        ownershipValues.put("AccountType.ownership.individual", false);

        financialFieldsValues.put("AccountType.financialFields.Advertiser", true);
        financialFieldsValues.put("AccountType.financialFields.Agency", false);

        invoicingValues.put("AccountType.invoicing.perAdvertiser", true);
        invoicingValues.put("AccountType.invoicing.perCampaign", false);

        inputRatesAndAmountsValues.put("AccountType.inputRatesAndAmounts.Gross", true);
        inputRatesAndAmountsValues.put("AccountType.inputRatesAndAmounts.Net", false);
        invoiceCommissionValues.put("AccountType.commission.include", true);
        invoiceCommissionValues.put("AccountType.commission.notInclude", false);
        auctionRateValues.put("AccountType.auctionRate.Gross", "G");
        auctionRateValues.put("AccountType.auctionRate.Net", "N");

        showTagsValues.put("AccountType.show", true);
        showTagsValues.put("AccountType.hide", false);

        billingModelValues.put("AccountType.billingModel.advertiser", false);
        billingModelValues.put("AccountType.billingModel.publisher", true);
    }

    protected void populateUIControls() {
        if (entity.getAccountRole() == AccountRole.AGENCY
                || entity.getAccountRole() == AccountRole.ADVERTISER
                || entity.getAccountRole() == AccountRole.PUBLISHER) {
            populateRadioControls();
        }
    }

    // radio button value getters
    public Map<String, Boolean> getRadioButtonFlagValues() {
        return radioButtonFlagValues;
    }

    public Map<String, Boolean> getOwnershipValues() {
        return ownershipValues;
    }

    public Map<String, Boolean> getInvoiceCommissionValues() {
        return invoiceCommissionValues;
    }

    public Map<String, Boolean> getInputRatesAndAmountsValues() {
        return inputRatesAndAmountsValues;
    }

    public Map<String, String> getAuctionRateValues() {
        return auctionRateValues;
    }

    public Map<String, Boolean> getInvoicingValues() {
        return invoicingValues;
    }

    public Map<String, Boolean> getFinancialFieldsValues() {
        return financialFieldsValues;
    }

    public Map<String, Boolean> getShowTagsValues() {
        return showTagsValues;
    }

    public Map<String, Boolean> getBillingModelValues() {
        return billingModelValues;
    }

    // all flag getters goes here

    public Boolean getDisplayCampaignsFlag() {
        return getDisplayCPMFlag() || getDisplayCPCFlag() || getDisplayCPAFlag() || changesCheck.isDisplayCreativesExist() || changesCheck.isDisplayCampaignsExist();
    }

    public Boolean getDisplayCPMFlag() {
        return getEntity().isCPMFlag(CCGType.DISPLAY);
    }

    public Boolean getDisplayCPCFlag() {
        return getEntity().isCPCFlag(CCGType.DISPLAY);
    }

    public Boolean getDisplayCPAFlag() {
        return getEntity().isCPAFlag(CCGType.DISPLAY);
    }

    public Boolean getTextCampaingsFlag() {
        return getKeywordTargetedFlag() || getChannelTargetedFlag() || changesCheck.isTextCampaignsExist();
    }

    public Boolean getKeywordTargetedFlag() {
        return getEntity().isAllowTextKeywordAdvertisingFlag();
    }

    public Boolean getChannelTargetedFlag() {
        return getEntity().isAllowTextChannelAdvertisingFlag();
    }

    public Boolean getTextCPMFlag() {
        return getEntity().isCPMFlag(CCGType.TEXT);
    }

    public Boolean getTextCPCFlag() {
        return getEntity().isCPCFlag(CCGType.TEXT);
    }

    public Boolean getTextCPAFlag() {
        return getEntity().isCPAFlag(CCGType.TEXT);
    }

    public boolean isOwnership() {
        if (hasErrors() || getEntity().getId() != null) {
            return getEntity().isOwnershipFlag();
        }
        return true;
    }

    public boolean isAdvExclusionApprovalFlag() {
        return getEntity().getAdvExclusionApproval() == AdvExclusionsApprovalType.REJECTED ? true : false;
    }

    // setter flags goes here
    public void setDisplayCPMFlag(Boolean flag) {
        getEntity().setCPMFlag(CCGType.DISPLAY, flag);
    }

    public void setDisplayCPCFlag(Boolean flag) {
        getEntity().setCPCFlag(CCGType.DISPLAY, flag);
    }

    public void setDisplayCPAFlag(Boolean flag) {
        getEntity().setCPAFlag(CCGType.DISPLAY, flag);
    }

    public void setKeywordTargetedFlag(Boolean flag) {
        getEntity().setAllowTextKeywordAdvertisingFlag(flag);
    }

    public void setTextCPMFlag(Boolean flag) {
        getEntity().setCPMFlag(CCGType.TEXT, flag);
    }

    public void setTextCPCFlag(Boolean flag) {
        getEntity().setCPCFlag(CCGType.TEXT, flag);
    }

    public void setTextCPAFlag(Boolean flag) {
        getEntity().setCPAFlag(CCGType.TEXT, flag);
    }

    public void setOwnership(boolean flag) {
        getEntity().setOwnershipFlag(flag);
    }

    public void setAdvExclusionApprovalFlag(boolean flag) {
        getEntity().setAdvExclusionApproval(flag ? AdvExclusionsApprovalType.REJECTED : AdvExclusionsApprovalType.ACCEPTED);
    }

    // disabled flag check goes here
    private AccountTypeDisabledFields getChangesCheck() {
        if (changesCheck == null) {
            changesCheck = service.getAccountTypeChangesCheck(getEntity());
        }
        return changesCheck;
    }

    public boolean isWdTagsFlagDisabled() {
        return getChangesCheck().isWdTagsFlagDisabled();
    }

    public boolean isAllowPublisherInventoryEstimationDisabled() {
        return getChangesCheck().isPublisherInventoryEstimationFlagDisabled();
    }

    public boolean isSiteTargetingDisabled() {
        return getChangesCheck().isSiteTargetingFlagDisabled();
    }

    public boolean isAllowTextAdvertisingDisabled() {
        return getChangesCheck().isAllowTextAdvertisingFlagDisabled();
    }

    public Boolean isAdvExclusionSiteFlagDisabled() {
        return getChangesCheck().isAdvExclusionSiteFlagDisabled();
    }

    public Boolean isAdvExclusionTagFlagDisabled() {
        return getChangesCheck().isAdvExclusionSiteTagFlagDisabled();
    }

    public Boolean isFrequencyCapsFlagDisabled() {
        return getChangesCheck().isFreqCapsFlagDisabled();
    }

    public Boolean getDisplayCPMDisabled() {
        return getChangesCheck().isDisplayCPMDisabled();
    }

    public Boolean getDisplayCPCDisabled() {
        return getChangesCheck().isDisplayCPCDisabled();
    }

    public Boolean getDisplayCPADisabled() {
        return getChangesCheck().isDisplayCPADisabled();
    }

    public Boolean isDisplayCreativesExist() {
        return getChangesCheck().isDisplayCreativesExist();
    }

    public Boolean isDisplayCampaignsExist() {
        return getChangesCheck().isDisplayCampaignsExist();
    }

    public Boolean isTextCampaignsExist() {
        return getChangesCheck().isTextCampaignsExist();
    }

    public Boolean isFinancialFieldsDisabled() {
        return getChangesCheck().isAgencyFinancialFieldsFlagDisabled();
    }

    public Boolean isInvoicingDisabled() {
        return getChangesCheck().isPerCampaignInvoicingFlagDisabled();
    }

    public Boolean isInputRatesAndAmountsDisabled() {
        return getChangesCheck().isInputRatesAndAmountsFlagDisabled();
    }

    public Boolean isInvoiceCommissionDisabled() {
        return getChangesCheck().isInvoiceCommissionFlagDisabled();
    }

    public Boolean getTextCPMDisabled() {
        return getChangesCheck().isTextCPMDisabled();
    }

    public Boolean getTextCPCDisabled() {
        return getChangesCheck().isTextCPCDisabled();
    }

    public Boolean getTextCPADisabled() {
        return getChangesCheck().isTextCPADisabled();
    }

    public Boolean getKeywordTargetedDisabled() {
        return getChangesCheck().isAllowTextKeywordAdvertisingFlagDisabled();
    }

    public Boolean getAdvExclusionApprovalDisabled() {
        return getChangesCheck().isAllowAdvExclusionApprovalDisabled();
    }

    public boolean isIOManagementDisabled() {
        return getChangesCheck().isIOManagementDisabled();
    }

    public boolean isBillingModelDisabled() {
        return getChangesCheck().isBillingModelFlagDisabled();
    }

    public Map<CheckNumber, CheckEntry> getChannelChecks() {
        AccountType accountType = getModel();
        if (channelChecks == null) {
            channelChecks = new LinkedHashMap<>();
            for (CheckNumber check : CheckNumber.values()) {
                TimeSpan originalValue = accountType.getChannelCheckByNum(check.value());
                if (this instanceof EditAccountTypeAction) {
                    channelChecks.put(check,
                        new CheckEntry(originalValue != null ? originalValue : check.getDefaultTimeSpan()));
                } else {
                    channelChecks.put(check, originalValue != null ? new CheckEntry(originalValue) : new CheckEntry());
                }
            }
        }
        return channelChecks;
    }

    public void setChannelChecks(Map<CheckNumber, CheckEntry> channelChecksMap) {
        this.channelChecks = channelChecksMap;
    }

    public Map<CheckNumber, CheckEntry> getCampaignChecks() {
        AccountType accountType = getModel();
        if (campaignChecks == null) {
            campaignChecks = new LinkedHashMap<>();
            for (CheckNumber check : CheckNumber.values()) {
                TimeSpan originalValue = accountType.getCampaignCheckByNum(check.value());
                campaignChecks.put(check,
                    new CheckEntry(originalValue != null ? originalValue : check.getDefaultTimeSpan()));
            }
        }

        return campaignChecks;
    }

    public void setCampaignChecks(Map<CheckNumber, CheckEntry> campaignChecksMap) {
        this.campaignChecks = campaignChecksMap;
    }

    @Override
    public void invalid() throws Exception {
        initDeviceHelper();
    }

    public DeviceTargetingHelper getDeviceHelper() {
        if (deviceHelper == null) {
            initDeviceHelper();
        }
        return deviceHelper;
    }

    public void setDeviceHelper(DeviceTargetingHelper deviceHelper) {
        this.deviceHelper = deviceHelper;
    }

    protected void initDeviceHelper() {
        deviceHelper = new DeviceTargetingHelper(getModel().getDeviceChannels(), new Filter<TreeNode<EntityTO>>() {
            @Override
            public boolean accept(TreeNode<EntityTO> node) {
                return node.getElement().getStatus().equals(Status.ACTIVE);
            }
        });
    }
}
