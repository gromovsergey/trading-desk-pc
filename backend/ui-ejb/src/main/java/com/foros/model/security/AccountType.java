package com.foros.model.security;

import com.foros.annotations.Audit;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.audit.serialize.serializer.primitive.TimeSpanAuditSerializer;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.VersionEntityBase;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.template.Template;
import com.foros.model.time.TimeSpan;
import com.foros.security.AccountRole;
import com.foros.util.FlagsUtil;
import com.foros.util.TriggerUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "ACCOUNTTYPE")
@NamedQueries( {
    @NamedQuery(name = "AccountType.findAll", query = "SELECT a FROM AccountType a ORDER BY a.name"),
    @NamedQuery(name = "AccountType.findById", query = "SELECT a FROM AccountType a WHERE a.id = :id"),
    @NamedQuery(name = "AccountType.findByName", query = "SELECT a FROM AccountType a WHERE a.name = :name"),
    @NamedQuery(name = "AccountType.findByRole", query = "SELECT a FROM AccountType a WHERE a.accountRole = :role"),
    @NamedQuery(name = "AccountType.findIndexByRole", query = "SELECT DISTINCT NEW com.foros.session.NamedTO( a.id, a.name) FROM AccountType a WHERE a.accountRole = :role")
})
public class AccountType extends VersionEntityBase implements IdNameEntity, Serializable, DeviceTargetingEntity {
    /** 0 - Agency, 1 - Advertiser  (advertisers) */
    public static final long FINANCIAL_FIELDS_FLAG = 0x02;

    /** 0 - Per Campaign, 1 - Per Advertiser (advertisers) */
    public static final long INVOICING_FLAG = 0x04;

    /** 0 - Net, 1 - Gross (advertisers) */
    public static final long INPUT_RATES_AND_AMOUNTS_FLAG = 0x10;
    public static final long FREQ_CAPS_FLAG = 0x08;
    public static final long SITE_TARGETING_FLAG = 0x20;
    /** 0 - Disabled, 1 - Enabled for publisher accounts */
    public static final long PUBLISHER_INVENTORY_ESTIMATION_FLAG = 0x40;
    public static final long OWNERSHIP = 0x800;

    /**  0 - not include, 1 - include (advertisers) */
    public static final long INVOICE_COMMISSION_FLAG = 0x1000;
    public static final long WDTAGS_FLAG = 0x1;

    public static final long ADVANCED_REPORTS_FLAG = 0x2000;

    public static final long CLICKS_DATA_FOR_EXTERNAL_FLAG = 0x4000;

    public static final long BILLING_MODEL_FLAG = 0x8000;

    @SequenceGenerator(name = "AccountTypeGen", sequenceName = "ACCOUNTTYPE_ACCOUNT_TYPE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountTypeGen")
    @IdConstraint
    @Column(name = "ACCOUNT_TYPE_ID", nullable = false)
    private Long id;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "NAME", nullable = false)
    private String name = "";

    @Column(name = "FLAGS", nullable = false)
    private long flags;

    @RequiredConstraint
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "ACCOUNT_ROLE_ID")
    private AccountRole accountRole;

    @JoinTable(name = "ACCOUNTTYPECREATIVESIZE",
        joinColumns = {@JoinColumn(name = "ACCOUNT_TYPE_ID", referencedColumnName = "ACCOUNT_TYPE_ID")},
        inverseJoinColumns = {@JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<CreativeSize> creativeSizes = new HashSet<CreativeSize>();

    @JoinTable(name = "ACCOUNTTYPECREATIVETEMPLATE",
        joinColumns = {@JoinColumn(name = "ACCOUNT_TYPE_ID", referencedColumnName = "ACCOUNT_TYPE_ID")},
        inverseJoinColumns = {@JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Template> templates = new LinkedHashSet<Template>();

    @OneToMany(mappedBy = "accountType", fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    @Fetch(FetchMode.SUBSELECT)
    private Set<AccountTypeCCGType> ccgTypes = new LinkedHashSet<AccountTypeCCGType>();

    @Column(name = "MAX_KEYWORD_LENGTH")
    @RangeConstraint(min = "1", max = "" + TriggerUtil.MAX_KEYWORD_LENGTH)
    private Long maxKeywordLength;

    @Column(name = "MAX_URL_LENGTH")
    @RangeConstraint(min = "4", max = "" + TriggerUtil.MAX_URL_LENGTH)
    private Long maxUrlLength;

    @Column(name = "MAX_KEYWORDS_PER_GROUP")
    @RangeConstraint(min = "1", max = "9999999999")
    private Long maxKeywordsPerGroup;

    @Column(name = "MAX_KEYWORDS_PER_CHANNEL")
    @RangeConstraint(min = "1", max = "9999999999")
    private Long maxKeywordsPerChannel;

    @Column(name = "MAX_URLS_PER_CHANNEL")
    @RangeConstraint(min = "1", max = "9999999999")
    private Long maxUrlsPerChannel;

    @Column(name = "SHOW_IFRAME_TAG")
    private Boolean showIframeTag;

    @Column(name = "SHOW_BROWSER_PASSBACK_TAG")
    private Boolean showBrowserPassbackTag;

    @Column(name  = "AUCTION_RATE")
    private char auctionRate = 'N';

    @Column(name = "ADV_EXCLUSION_APPROVAL")
    private Character advExclusionApproval;

    @Column(name = "ADV_EXCLUSIONS")
    private Character advExclusions;

    @Column(name = "IO_MANAGEMENT")
    private Boolean ioManagement;

    @JoinTable(name = "AccountTypeDeviceChannel",
            joinColumns = {@JoinColumn(name = "ACCOUNT_TYPE_ID", referencedColumnName = "ACCOUNT_TYPE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DEVICE_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<DeviceChannel> deviceChannels = new LinkedHashSet<DeviceChannel>();

    @Column(name = "CAMPAIGN_CHECK_ON")
    private Boolean campaignCheck;

    @Column(name = "CHANNEL_CHECK_ON")
    private Boolean channelCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CAMPAIGN_CHECK_1")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan campaignFirstCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CAMPAIGN_CHECK_2")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan campaignSecondCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CAMPAIGN_CHECK_3")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan campaignThirdCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CHANNEL_CHECK_1")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan channelFirstCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CHANNEL_CHECK_2")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan channelSecondCheck;

    @Audit(serializer = TimeSpanAuditSerializer.class)
    @Column(name = "CHANNEL_CHECK_3")
    @Type(type = "com.foros.persistence.hibernate.type.TimeSpanSecondsType")
    private TimeSpan channelThirdCheck;

    public AccountType() {
    }

    public AccountType(Long id, String name, long flags, AccountRole accountRole) {
        this.id = id;
        this.name = name;
        this.flags = flags;
        this.accountRole = accountRole;
    }

    public AccountType(Long accountTypeId) {
        this.id = accountTypeId;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long accountTypeId) {
        this.id = accountTypeId;
        this.registerChange("id");
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public long getFlags() {
        return this.flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    public AccountRole getAccountRole() {
        return this.accountRole;
    }

    public void setAccountRole(AccountRole role) {
        this.accountRole = role;
        this.registerChange("accountRole");
    }

    @Transient
    public String getDefaultName() {
        return getName();
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
        if (!(object instanceof AccountType)) {
            return false;
        }
        AccountType other = (AccountType)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    public Set<CreativeSize> getCreativeSizes() {
        return new ChangesSupportSet<CreativeSize>(this, "creativeSizes", creativeSizes);
    }

    public void setCreativeSizes(Set<CreativeSize> creativeSizes) {
        this.creativeSizes = creativeSizes;
        this.registerChange("creativeSizes");
    }

    public Set<Template> getTemplates() {
        return new ChangesSupportSet<Template>(this, "templates", templates);
    }

    public void setTemplates(Set<Template> templates) {
        this.templates = templates;
        this.registerChange("templates");
    }

    public boolean isFreqCapsFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), FREQ_CAPS_FLAG);
    }

    public void setFreqCapsFlag(boolean flag) {
        this.setFlags(FlagsUtil.set(getFlags(), FREQ_CAPS_FLAG, flag));
    }

    public boolean isAdvancedReportsFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), ADVANCED_REPORTS_FLAG);
    }

    public void setAdvancedReportsFlag(boolean flag) {
        this.setFlags(FlagsUtil.set(getFlags(), ADVANCED_REPORTS_FLAG, flag));
    }

    public boolean isClicksDataVisibleToExternal() {
        return FlagsUtil.get(getFlags(), CLICKS_DATA_FOR_EXTERNAL_FLAG);
    }

    public void setClicksDataVisibleToExternal(boolean flag) {
        this.setFlags(FlagsUtil.set(getFlags(), CLICKS_DATA_FOR_EXTERNAL_FLAG, flag));
    }

    public boolean isBillingModelFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), BILLING_MODEL_FLAG);
    }

    public void setBillingModelFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), BILLING_MODEL_FLAG, value));
    }

    @XmlTransient
    public Set<AccountTypeCCGType> getCcgTypes() {
        return new ChangesSupportSet<AccountTypeCCGType>(this, "ccgTypes", ccgTypes);
    }

    public void setCcgTypes(Set<AccountTypeCCGType> ccgTypes) {
        this.ccgTypes = ccgTypes;
        this.registerChange("ccgTypes");
    }

    private AccountTypeCCGType findCCGType(CCGType ccgType, TGTType tgtType, RateType rateType) {
        for (AccountTypeCCGType accountCcgType : ccgTypes) {
            if (accountCcgType.getCcgType().equals(ccgType) &&
                accountCcgType.getTgtType().equals(tgtType) &&
                accountCcgType.getRateType().equals(rateType)) {

                return accountCcgType;
            }
        }

        return null;
    }

    private void setCCGFlag(boolean flag, CCGType ccgType, TGTType tgtType, RateType rateType) {
        AccountTypeCCGType accountCcgType = findCCGType(ccgType, tgtType, rateType);
        if (flag) {
            if (accountCcgType == null) {
                accountCcgType = new AccountTypeCCGType(this, ccgType, tgtType, rateType);
                getCcgTypes().add(accountCcgType);
            }
            return;
        }

        if (accountCcgType != null) {
            getCcgTypes().remove(accountCcgType);
        }
    }

    public boolean isRateAllowed(CCGType ccgType, TGTType tgtType, RateType rateType) {
        return findCCGType(ccgType, tgtType, rateType) != null;
    }

    public boolean isCPMFlag(CCGType ccgType) {
        return isRateAllowed(ccgType, TGTType.CHANNEL, RateType.CPM);
    }

    public void setCPMFlag(CCGType ccgType, boolean flag) {
        setCCGFlag(flag, ccgType, TGTType.CHANNEL, RateType.CPM);
    }

    public boolean isCPCFlag(CCGType ccgType) {
        return isRateAllowed(ccgType, TGTType.CHANNEL, RateType.CPC);
    }

    public void setCPCFlag(CCGType ccgType, boolean flag) {
        setCCGFlag(flag, ccgType, TGTType.CHANNEL, RateType.CPC);
    }

    public boolean isCPAFlag(CCGType ccgType) {
        return isRateAllowed(ccgType, TGTType.CHANNEL, RateType.CPA);
    }

    public void setCPAFlag(CCGType ccgType, boolean flag) {
        setCCGFlag(flag, ccgType, TGTType.CHANNEL, RateType.CPA);
    }

    public boolean isAllowDisplayAdvertisingFlag() {
        return isCPMFlag(CCGType.DISPLAY) || isCPAFlag(CCGType.DISPLAY) || isCPCFlag(CCGType.DISPLAY);
    }

    public boolean isAllowTextKeywordAdvertisingFlag() {
        return findCCGType(CCGType.TEXT, TGTType.KEYWORD, RateType.CPC) != null;
    }

    public void setAllowTextKeywordAdvertisingFlag(boolean flag) {
        setCCGFlag(flag, CCGType.TEXT, TGTType.KEYWORD, RateType.CPC);
    }

    public boolean isAllowTextChannelAdvertisingFlag() {
        return isCPMFlag(CCGType.TEXT) || isCPAFlag(CCGType.TEXT) || isCPCFlag(CCGType.TEXT);
    }

    public boolean isAllowTextAdvertisingFlag() {
        return isAllowTextKeywordAdvertisingFlag() || isAllowTextChannelAdvertisingFlag();
    }

    public List<RateType> getAllowedRateTypes(CCGType ccgType, TGTType tgtType) {
        ArrayList<RateType> list = new ArrayList<RateType>();

        boolean isKeywordAdvFlag = TGTType.CHANNEL == tgtType ? false : ccgType == CCGType.TEXT && isAllowTextKeywordAdvertisingFlag();
        if (tgtType == TGTType.KEYWORD) {
            if (isKeywordAdvFlag) {
                list.add(RateType.CPC);
            }
            return list;
        }

        if (isCPMFlag(ccgType)) {
            list.add(RateType.CPM);
        }
        if (isKeywordAdvFlag || isCPCFlag(ccgType)) {
            list.add(RateType.CPC);
        }
        if (isCPAFlag(ccgType)) {
            list.add(RateType.CPA);
        }
        return list;
    }

    public List<RateType> getAllowedRateTypes(CCGType ccgType) {
        return getAllowedRateTypes(ccgType, null);
    }

    public boolean isSiteTargetingFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), SITE_TARGETING_FLAG);
    }

    public void setSiteTargetingFlag(boolean flag) {
        this.setFlags(FlagsUtil.set(getFlags(), SITE_TARGETING_FLAG, flag));
    }

    public boolean isPublisherInventoryEstimationFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), PUBLISHER_INVENTORY_ESTIMATION_FLAG);
    }

    public void setPublisherInventoryEstimationFlag(boolean flag) {
        this.setFlags(FlagsUtil.set(getFlags(), PUBLISHER_INVENTORY_ESTIMATION_FLAG, flag));
    }

    public boolean isOwnershipFlag() {
        // Default value for Ownership flag is Company i.e. true
        return FlagsUtil.get(getFlags(), OWNERSHIP);
    }

    public void setOwnershipFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), OWNERSHIP, value));
    }

    public boolean isWdTagsFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), WDTAGS_FLAG);
    }

    public void setWdTagsFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), WDTAGS_FLAG, value));
    }

    @Override
    public String toString() {
        return "com.foros.model.security.AccountType[id=" + getId() + "]";
    }

    public boolean isAgencyFinancialFieldsFlag() {
        return !isFinancialFieldsFlag();
    }

    public boolean isFinancialFieldsFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), FINANCIAL_FIELDS_FLAG);
    }

    public void setFinancialFieldsFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), FINANCIAL_FIELDS_FLAG, value));
    }

    public boolean isPerCampaignInvoicingFlag() {
        // Default value is false
        return !isInvoicingFlag();
    }

    public boolean isInvoicingFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), INVOICING_FLAG);
    }

    public void setInvoicingFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), INVOICING_FLAG, value));
    }
    public boolean isInputRatesAndAmountsFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), INPUT_RATES_AND_AMOUNTS_FLAG);
    }

    public void setInputRatesAndAmountsFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), INPUT_RATES_AND_AMOUNTS_FLAG, value));
    }

    public boolean isInvoiceCommissionFlag() {
        // Default value is false
        return FlagsUtil.get(getFlags(), INVOICE_COMMISSION_FLAG);
    }

    public void setInvoiceCommissionFlag(boolean value) {
        this.setFlags(FlagsUtil.set(getFlags(), INVOICE_COMMISSION_FLAG, value));
    }

    public Long getMaxKeywordLength() {
        return maxKeywordLength;
    }

    public void setMaxKeywordLength(Long maxKeywordLength) {
        this.maxKeywordLength = maxKeywordLength;
        this.registerChange("maxKeywordLength");
    }

    public Long getMaxUrlLength() {
        return maxUrlLength;
    }

    public void setMaxUrlLength(Long maxUrlLength) {
        this.maxUrlLength = maxUrlLength;
        this.registerChange("maxUrlLength");
    }

    public Long getMaxKeywordsPerGroup() {
        return maxKeywordsPerGroup;
    }

    public void setMaxKeywordsPerGroup(Long maxKeywordsPerGroup) {
        this.maxKeywordsPerGroup = maxKeywordsPerGroup;
        this.registerChange("maxKeywordsPerGroup");
    }

    public Long getMaxKeywordsPerChannel() {
        return maxKeywordsPerChannel;
    }

    public void setMaxKeywordsPerChannel(Long maxKeywordsPerChannel) {
        this.maxKeywordsPerChannel = maxKeywordsPerChannel;
        this.registerChange("maxKeywordsPerChannel");
    }

    public Long getMaxUrlsPerChannel() {
        return maxUrlsPerChannel;
    }

    public void setMaxUrlsPerChannel(Long maxUrlsPerChannel) {
        this.maxUrlsPerChannel = maxUrlsPerChannel;
        this.registerChange("maxUrlsPerChannel");
    }

    public Boolean getShowIframeTag() {
        return showIframeTag;
    }

    public void setShowIframeTag(Boolean showIframeTag) {
        this.showIframeTag = showIframeTag;
        this.registerChange("showIframeTag");
    }

    public Boolean getShowBrowserPassbackTag() {
        return showBrowserPassbackTag;
    }

    public void setShowBrowserPassbackTag(Boolean showBrowserPassbackTag) {
        this.showBrowserPassbackTag = showBrowserPassbackTag;
        this.registerChange("showBrowserPassbackTag");
    }

    /**
     * @return true = Gross, false = Net
     */
    public boolean isInputRatesAndAmountsGross() {
        return isInputRatesAndAmountsFlag();
    }

    public char getAuctionRate() {
        return auctionRate;
    }

    public void setAuctionRate(char auctionRate) {
        this.auctionRate = auctionRate;
        this.registerChange("auctionRate");
    }

    public AdvExclusionsType getAdvExclusions() {
        if (advExclusions != null) {
            return AdvExclusionsType.valueOf(advExclusions);
        }
        return null;
    }

    public void setAdvExclusions(AdvExclusionsType advExclusions) {
        this.advExclusions = (advExclusions == null) ? null : advExclusions.getLetter();
        this.registerChange("advExclusions");
    }

    public boolean isAdvExclusionSiteFlag() {
        return getAdvExclusions() == AdvExclusionsType.SITE_LEVEL ||
                isAdvExclusionSiteTagFlag();
    }

    public boolean isAdvExclusionSiteTagFlag() {
        return getAdvExclusions() == AdvExclusionsType.SITE_AND_TAG_LEVELS;
    }

    public boolean isAdvExclusionFlag() {
        return isAdvExclusionSiteFlag();
    }

    public AdvExclusionsApprovalType getAdvExclusionApproval() {
        if (advExclusionApproval != null) {
            return AdvExclusionsApprovalType.valueOf(advExclusionApproval);
        }
        return null;
    }

    public void setAdvExclusionApproval(AdvExclusionsApprovalType advExclusionApproval) {
        this.advExclusionApproval = (advExclusionApproval == null) ? null : advExclusionApproval.getLetter();
        this.registerChange("advExclusionApproval");
    }

    public boolean isAdvExclusionApprovalAllowed() {
        if (isAdvExclusionFlag() && getAdvExclusionApproval() == AdvExclusionsApprovalType.ACCEPTED) {
            return true;
        }
        return false;
    }

    public Boolean getIoManagement() {
        return ioManagement;
    }

    public void setIoManagement(Boolean ioManagement) {
        this.ioManagement = ioManagement;
        this.registerChange("ioManagement");
    }

    @Override
    @XmlTransient
    public Set<DeviceChannel> getDeviceChannels() {
        return new ChangesSupportSet<DeviceChannel>(this, "deviceChannels", deviceChannels);
    }

    public void setDeviceChannels(Set<DeviceChannel> deviceChannels) {
        this.deviceChannels = deviceChannels;
        this.registerChange("deviceChannels");
    }

    public boolean isCampaignCheck() {
        return campaignCheck == null? false: campaignCheck;
    }

    public void setCampaignCheck(Boolean campaignCheck) {
        this.campaignCheck = campaignCheck;
        this.registerChange("campaignCheck");
    }

    public boolean isChannelCheck() {
        return channelCheck == null? false: channelCheck;
    }

    public void setChannelCheck(Boolean channelCheck) {
        this.channelCheck = channelCheck;
        this.registerChange("channelCheck");
    }

    public TimeSpan getCampaignFirstCheck() {
        return campaignFirstCheck;
    }

    public void setCampaignFirstCheck(TimeSpan campaignFirstCheck) {
        this.campaignFirstCheck = campaignFirstCheck;
        this.registerChange("campaignFirstCheck");
    }

    public TimeSpan getCampaignSecondCheck() {
        return campaignSecondCheck;
    }

    public void setCampaignSecondCheck(TimeSpan campaignSecondCheck) {
        this.campaignSecondCheck = campaignSecondCheck;
        this.registerChange("campaignSecondCheck");
    }

    public TimeSpan getCampaignThirdCheck() {
        return campaignThirdCheck;
    }

    public void setCampaignThirdCheck(TimeSpan campaignThirdCheck) {
        this.campaignThirdCheck = campaignThirdCheck;
        this.registerChange("campaignThirdCheck");
    }

    public TimeSpan getChannelFirstCheck() {
        return channelFirstCheck;
    }

    public void setChannelFirstCheck(TimeSpan channelFirstCheck) {
        this.channelFirstCheck = channelFirstCheck;
        this.registerChange("channelFirstCheck");
    }

    public TimeSpan getChannelSecondCheck() {
        return channelSecondCheck;
    }

    public void setChannelSecondCheck(TimeSpan channelSecondCheck) {
        this.channelSecondCheck = channelSecondCheck;
        this.registerChange("channelSecondCheck");
    }

    public TimeSpan getChannelThirdCheck() {
        return channelThirdCheck;
    }

    public void setChannelThirdCheck(TimeSpan channelThirdCheck) {
        this.channelThirdCheck = channelThirdCheck;
        this.registerChange("channelThirdCheck");
    }

    public TimeSpan getCampaignCheckByNum(int intervalNum) {
        if (intervalNum == 1) {
            return getCampaignFirstCheck();
        }
        if (intervalNum == 2) {
            return getCampaignSecondCheck();
        }
        return getCampaignThirdCheck();
    }

    public TimeSpan getChannelCheckByNum(int intervalNum) {
        if (intervalNum == 1) {
            return getChannelFirstCheck();
        }
        if (intervalNum == 2) {
            return getChannelSecondCheck();
        }
        return getChannelThirdCheck();
    }

    public void setChannelCheckByNum(int intervalNum, TimeSpan value) {
        switch (intervalNum) {
        case 1:
            setChannelFirstCheck(value);
            break;
        case 2:
            setChannelSecondCheck(value);
            break;
        default:
            setChannelThirdCheck(value);
        }
    }

    public void setCampaignCheckByNum(int intervalNum, TimeSpan value) {
        switch (intervalNum) {
        case 1:
            setCampaignFirstCheck(value);
            break;
        case 2:
            setCampaignSecondCheck(value);
            break;
        default:
            setCampaignThirdCheck(value);
        }
    }

    public SizeType findSizeTypeByName(final String sizeTypeName) {
        List<CreativeSize> sizes = findSizesBySizeTypeName(sizeTypeName);
        return sizes.isEmpty() ? null : sizes.get(0).getSizeType();
    }

    public List<CreativeSize> findSizesBySizeTypeName(final String sizeTypeName) {
        ArrayList<CreativeSize> res = new ArrayList<>(getCreativeSizes());
        CollectionUtils.filter(res, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                CreativeSize size = (CreativeSize) object;
                return size.getStatus() != Status.DELETED && size.getSizeType().getDefaultName().equals(sizeTypeName);
            }
        });
        return res;
    }
}
