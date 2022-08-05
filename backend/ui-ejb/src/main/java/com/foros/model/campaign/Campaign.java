package com.foros.model.campaign;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.AdvertiserAgencyAccountXmlAdapter;
import com.foros.jaxb.adapters.ChannelLinkXmlAdapter;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.jaxb.adapters.MarketpalceXmlAdapter;
import com.foros.jaxb.adapters.UserLinkXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntityBase;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.channel.Channel;
import com.foros.model.finance.Invoice;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.security.User;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NoSecondsConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@NamedQueries({
    @NamedQuery(name="Campaign.findUndeletedByAccountId", query="SELECT c FROM Campaign c WHERE c.account.id = :accountId AND c.status != 'D'")
})
@Entity
@Table(name = "CAMPAIGN")
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlRootElement(name = "campaign")
@XmlType(propOrder = {
        "id",
        "account",
        "name",
        "campaignType",
        "salesManager",
        "soldToUser",
        "billToUser",
        "budget",
        "commission",
        "dateEnd",
        "dateStart",
        "deliveryPacing",
        "dailyBudget",
        "campaignSchedules",
        "frequencyCap",
        "marketplaceType",
        "maxPubShare",
        "excludedChannels",
        "bidStrategy"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Campaign extends DisplayStatusEntityBase implements OwnedStatusable<AdvertiserAccount>, Serializable, IdNameEntity, FrequencyCapEntity {
    @GenericGenerator(name = "CampaignGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "CAMPAIGN_CAMPAIGN_ID_SEQ")})
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignGen")
    @Column(name = "CAMPAIGN_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @IdConstraint
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FLAGS")
    private Long flags;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private AdvertiserAccount account;

    @JoinColumn(name = "FREQ_CAP_ID", referencedColumnName = "FREQ_CAP_ID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private FrequencyCap frequencyCap;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "campaign")
    @OrderBy("name ASC")
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<CampaignCreativeGroup> creativeGroups = new LinkedHashSet<CampaignCreativeGroup>();

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @OrderBy("invoiceDate ASC")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE, type = LinkedHashSet.class)
    private Set<Invoice> invoices = new LinkedHashSet<Invoice>();

    @JoinColumn(name = "SALES_MANAGER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW, mandatory = false)
    private User salesManager;

    @JoinColumn(name = "SOLD_TO_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW, mandatory = true)
    private User soldToUser;

    @JoinColumn(name = "BILL_TO_USER_ID", referencedColumnName = "USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW, mandatory = true)
    private User billToUser;

    @Column(name = "BUDGET_MANUAL", precision = 14, scale = 5)
    private BigDecimal budget;

    @Column(name = "BUDGET", insertable = false, updatable = false)
    private BigDecimal totalBudget;

    @Column(name = "COMMISSION", precision = 10, scale = 5, nullable = false)
    private BigDecimal commission = BigDecimal.ZERO;

    @Column(name="MAX_PUB_SHARE", precision = 3, scale = 2, nullable = false)
    private BigDecimal maxPubShare = BigDecimal.ONE;

    @RequiredConstraint
    @NoSecondsConstraint
    @Column(name = "DATE_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStart;

    @NoSecondsConstraint
    @Column(name = "DATE_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnd;

    @Column(name = "MARKETPLACE")
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.account.MarketplaceType"),
            @Parameter(name = "nullValue", value = "NOT_SET")
    })
    private MarketplaceType marketplaceType  = MarketplaceType.NOT_SET;

    @RequiredConstraint
    @Column(name = "CAMPAIGN_TYPE", nullable = false, updatable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.campaign.CampaignType"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter") })
    private CampaignType campaignType;

    @Column(name = "DAILY_BUDGET", nullable = true)
    private BigDecimal dailyBudget;

    @RequiredConstraint
    @Column(name = "DELIVERY_PACING", nullable = false)
    private char deliveryPacing = DeliveryPacing.UNRESTRICTED.getLetter();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "campaign", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    // @OrderBy("timeFrom ASC") becomes first order by condition in com.foros.session.query.campaign.CampaignQueryImpl
    private Set<CampaignSchedule> campaignSchedules = new LinkedHashSet<CampaignSchedule>();

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<CampaignAllocation> allocations = new LinkedHashSet<CampaignAllocation>();

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<CampaignCreditAllocation> creditAllocations = new LinkedHashSet<CampaignCreditAllocation>();

    @JoinTable(name = "CampaignExcludedChannel",
            joinColumns = {@JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Channel.class)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Channel> excludedChannels = new LinkedHashSet<Channel>();

    @Column(name = "BID_STRATEGY")
    @Enumerated(EnumType.STRING)
    private CampaignBidStrategy bidStrategy = CampaignBidStrategy.CTR_BY_AMOUNT;


    public static final BigDecimal BUDGET_MAX = new BigDecimal("1000000000");
    public static final BigDecimal BUDGET_UNLIMITED = null;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "campaign.displaystatus.live");
    public static final DisplayStatus LIVE_NEED_ATT = new DisplayStatus(2L, DisplayStatus.Major.LIVE_NEED_ATT, "campaign.displaystatus.live_na");
    public static final DisplayStatus NOT_LIVE_NEED_ATT = new DisplayStatus(3L, DisplayStatus.Major.NOT_LIVE, "campaign.displaystatus.not_live_na");
    public static final DisplayStatus NO_AVAIL_BUDGET = new DisplayStatus(4L, DisplayStatus.Major.NOT_LIVE, "campaign.displaystatus.no_avail_budget");
    public static final DisplayStatus NO_ACTIVE_GROUPS = new DisplayStatus(5L, DisplayStatus.Major.INACTIVE, "campaign.displaystatus.no_active_groups");
    public static final DisplayStatus DELETED = new DisplayStatus(6L, DisplayStatus.Major.DELETED, "campaign.displaystatus.deleted");
    public static final DisplayStatus INACTIVE = new DisplayStatus(7L, DisplayStatus.Major.INACTIVE, "campaign.displaystatus.inactive");
    public static final DisplayStatus DATE_NOT_IN_RANGE = new DisplayStatus(8L, DisplayStatus.Major.NOT_LIVE, "campaign.displaystatus.date_not_in_range");

    public static Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        LIVE_NEED_ATT,
        NOT_LIVE_NEED_ATT,
        NO_AVAIL_BUDGET,
        NO_ACTIVE_GROUPS,
        DELETED,
        INACTIVE,
        DATE_NOT_IN_RANGE
    );

    public static DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(displayStatusId);
    }

    public static Collection<DisplayStatus> getAvailableDisplayStatuses() {
        return displayStatusMap.values();
    }

    public Campaign() {
    }

    public Campaign(Long id) {
        this.id = id;
    }

    public Campaign(Long id, String name) {
        this();
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    @XmlTransient
    public Long getFlags() {
        return this.flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(AdvertiserAgencyAccountXmlAdapter.class)
    public AdvertiserAccount getAccount() {
        return this.account;
    }

    @Override
    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    @Override
    public void setFrequencyCap(FrequencyCap freqCapId) {
        this.frequencyCap = freqCapId;
        this.registerChange("frequencyCap");
    }

    public void setAccount(AdvertiserAccount account) {
        this.account = account;
        this.registerChange("account");
    }

    @XmlTransient
    public Set<CampaignCreativeGroup> getCreativeGroups() {
        return new ChangesSupportSet<CampaignCreativeGroup>(this, "creativeGroups", creativeGroups);
    }

    public void setCreativeGroups(Set<CampaignCreativeGroup> creativeGroups) {
        this.creativeGroups = creativeGroups;
        this.registerChange("creativeGroups");
    }

    @XmlTransient
    public Set<Invoice> getInvoices() {
        return new ChangesSupportSet<Invoice>(this, "invoices", invoices);
    }

    public void setInvoices(Set<Invoice> invoices) {
        this.invoices = invoices;
        this.registerChange("invoices");
    }

    @XmlElement
    @XmlJavaTypeAdapter(UserLinkXmlAdapter.class)
    public User getSalesManager() {
        return salesManager;
    }

    public void setSalesManager(User salesManager) {
        this.salesManager = salesManager;
        this.registerChange("salesManager");
    }

    @XmlElement
    @XmlJavaTypeAdapter(UserLinkXmlAdapter.class)
    public User getSoldToUser() {
        return soldToUser;
    }

    public void setSoldToUser(User soldToUser) {
        this.soldToUser = soldToUser;
        this.registerChange("soldToUser");
    }

    @XmlElement
    @XmlJavaTypeAdapter(UserLinkXmlAdapter.class)
    public User getBillToUser() {
        return billToUser;
    }

    public void setBillToUser(User billToUser) {
        this.billToUser = billToUser;
        this.registerChange("billToUser");
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
        this.registerChange("budget");
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
        this.registerChange("commission");
    }

    public BigDecimal getMaxPubShare() {
        return maxPubShare;
    }

    public void setMaxPubShare(BigDecimal maxPubShare) {
        this.maxPubShare = maxPubShare;
        this.registerChange("maxPubShare");
    }

    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
        this.registerChange("dateStart");
    }

    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getDateEnd() {
        return this.dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
        this.registerChange("dateEnd");
    }


    public MarketplaceType getMarketplaceType() {
        return marketplaceType == null ? MarketplaceType.NOT_SET : marketplaceType;
    }

    @XmlJavaTypeAdapter(MarketpalceXmlAdapter.class)
    public void setMarketplaceType(MarketplaceType marketplaceType) {
        if (marketplaceType == null) {
            throw new NullPointerException();
        }
        this.marketplaceType = marketplaceType;
        this.registerChange("marketplaceType");
    }

    public MarketplaceTypeTO getMarketplace() {
        return new MarketplaceTypeTO();
    }

    public CampaignType getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(CampaignType campaignType) {
        if(campaignType == null) {
            throw new NullPointerException();
        }
        this.campaignType = campaignType;
        this.registerChange("campaignType");
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
        this.registerChange("dailyBudget");
    }

    public DeliveryPacing getDeliveryPacing() {
        return DeliveryPacing.valueOf(deliveryPacing);
    }

    public void setDeliveryPacing(DeliveryPacing deliveryPacing) {
        this.deliveryPacing = deliveryPacing.getLetter();
        this.registerChange("deliveryPacing");
    }

    @XmlTransient
    public Set<CampaignAllocation> getAllocations() {
        return ChangesSupportSet.wrap(this, "allocations", allocations);
    }

    public void setAllocations(Set<CampaignAllocation> allocations) {
        this.allocations = allocations;
        this.registerChange("allocations");
    }

    @XmlElementWrapper(name = "excludedChannels")
    @XmlElement(name = "channel")
    @XmlJavaTypeAdapter(ChannelLinkXmlAdapter.class)
    public Set<Channel> getExcludedChannels() {
        return ChangesSupportSet.wrap(this, "excludedChannels", excludedChannels);
    }

    public void setExcludedChannels(Set<? extends Channel> excludedChannels) {
        //noinspection unchecked
        this.excludedChannels = (Set<Channel>) excludedChannels;
        this.registerChange("excludedChannels");
    }

    @Override
    public Status getParentStatus() {
        return account.getInheritedStatus();
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
        if (!(object instanceof Campaign)) {
            return false;
        }

        Campaign other = (Campaign)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.campaign.Campaign[id=" + getId() + "]";
    }

    public static boolean isBudgetUnlimited(BigDecimal budget, CampaignType campaignType) {
        return (budget == null && campaignType != CampaignType.TEXT);
    }

    private class MarketplaceTypeTO {

        public boolean isInFOROS() {
            return marketplaceType != null && marketplaceType.isInFOROS();
        }

        public void setInFOROS(boolean inFOROS) {
            if (marketplaceType == null) {
                marketplaceType = inFOROS ? MarketplaceType.FOROS : MarketplaceType.NOT_SET;
            } else {
                boolean inWG = marketplaceType.isInWG();

                if (inFOROS && inWG) {
                    marketplaceType = MarketplaceType.ALL;
                } else if (inWG) {
                    marketplaceType = MarketplaceType.WG;
                } else if (inFOROS) {
                    marketplaceType = MarketplaceType.FOROS;
                } else {
                    marketplaceType = MarketplaceType.NOT_SET;
                }
            }
            registerChange("marketplaceType");
        }

        public boolean isInWG() {
            return marketplaceType != null && marketplaceType.isInWG();
        }

        public void setInWG(boolean inWG) {
            if (marketplaceType == null) {
                marketplaceType = inWG ? MarketplaceType.WG : MarketplaceType.NOT_SET;
            } else {
                boolean inFOROS = marketplaceType.isInFOROS();

                if (inFOROS && inWG) {
                    marketplaceType = MarketplaceType.ALL;
                } else if (inWG) {
                    marketplaceType = MarketplaceType.WG;
                } else if (inFOROS) {
                    marketplaceType = MarketplaceType.FOROS;
                } else {
                    marketplaceType = MarketplaceType.NOT_SET;
                }
            }
            registerChange("marketplaceType");
        }
    }

    @XmlElementWrapper(name = "campaignSchedules")
    @XmlElement(name = "campaignSchedule")
    public Set<CampaignSchedule> getCampaignSchedules() {
        return new ChangesSupportSet<CampaignSchedule>(this, "campaignSchedules", campaignSchedules);
    }

    public void setCampaignSchedules(Set<CampaignSchedule> campaignSchedules) {
        this.campaignSchedules = campaignSchedules;
        this.registerChange("campaignSchedules");
    }

    @XmlTransient
    public Set<CampaignCreditAllocation> getCreditAllocations() {
        return new ChangesSupportSet<CampaignCreditAllocation>(this, "creditAllocations", creditAllocations);
    }

    public void setCreditAllocations(Set<CampaignCreditAllocation> creditAllocations) {
        this.creditAllocations = creditAllocations;
        this.registerChange("creditAllocations");
    }

    public CampaignBidStrategy getBidStrategy() {
        return bidStrategy;
    }

    public void setBidStrategy(CampaignBidStrategy bidStrategy) {
        this.bidStrategy = bidStrategy;
        this.registerChange("bidStrategy");
    }
}
