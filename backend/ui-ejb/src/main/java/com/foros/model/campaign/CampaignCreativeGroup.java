package com.foros.model.campaign;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.ForceFieldChangeEntityChange;
import com.foros.jaxb.adapters.ActionLinkXmlAdapter;
import com.foros.jaxb.adapters.CampaignXmlAdapter;
import com.foros.jaxb.adapters.CcgRateXmlAdapter;
import com.foros.jaxb.adapters.ChannelLinkXmlAdapter;
import com.foros.jaxb.adapters.ColocationLinkXmlAdapter;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.jaxb.adapters.DeviceChannelLinkXmlAdapter;
import com.foros.jaxb.adapters.GeoChannelLinkXmlAdapter;
import com.foros.jaxb.adapters.OptInStatusTargetingXmlAdapter;
import com.foros.jaxb.adapters.SiteLinkXmlAdapter;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Flags;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.OwnedApprovable;
import com.foros.model.RegularCheckable;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.isp.Colocation;
import com.foros.model.security.DeviceTargetingEntity;
import com.foros.model.security.User;
import com.foros.model.site.Site;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NoSecondsConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CAMPAIGNCREATIVEGROUP")
@NamedQueries({
    @NamedQuery(name = "CampaignCreativeGroup.findUndeletedByCampaignId", query = "SELECT c FROM CampaignCreativeGroup c LEFT JOIN FETCH c.ccgRate WHERE c.campaign.id = :campaignId AND c.status <> 'D'"),
    @NamedQuery(name = "CampaignCreativeGroup.entityTO.findByCampaignId", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM CampaignCreativeGroup c WHERE c.campaign.id = :campaignId ORDER BY c.name"),
    @NamedQuery(name = "CampaignCreativeGroup.entityTO.findByCampaignIdForExternal", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM CampaignCreativeGroup c WHERE c.campaign.id = :campaignId and c.status <> 'D' ORDER BY c.name"),
    @NamedQuery(name = "CampaignCreativeGroup.entityTO.findByCampaignIdAndTargetType", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM CampaignCreativeGroup c WHERE c.campaign.id = :campaignId and c.tgtType = :targetType ORDER BY c.name"),
    @NamedQuery(name = "CampaignCreativeGroup.entityTO.findByCampaignIdAndTargetTypeForExternal", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM CampaignCreativeGroup c WHERE c.campaign.id = :campaignId and c.tgtType = :targetType and c.status <> 'D'  ORDER BY c.name")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING})
@AllowedQAStatuses(values = { ApproveStatus.APPROVED, ApproveStatus.DECLINED, ApproveStatus.HOLD })
@XmlRootElement
@XmlType(propOrder = {
        "id",
        "name",
        "campaign",
        "dateStart",
        "dateEnd",
        "linkedToCampaignEndDateFlag",
        "frequencyCap",
        "budget",
        "dailyBudget",
        "ccgSchedules",
        "deliveryPacing",
        "deliveryScheduleFlag",
        "ccgType",
        "ccgRate",
        "bidStrategy",
        "minCtrGoal",
        "tgtType",
        "channelTarget",
        "channel",
        "country",
        "geoChannels",
        "geoChannelsExcluded",
        "deviceChannels",
        "optInStatusTargeting",
        "minUidAge",
        "colocations",
        "sites",
        "actions"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@Audit(nodeFactory = ForceFieldChangeEntityChange.Factory.class)
public class CampaignCreativeGroup extends ApprovableEntity implements OwnedApprovable<AdvertiserAccount>, IdNameEntity, FrequencyCapEntity, DeviceTargetingEntity, Serializable, RegularCheckable {

    public static final int SEQUENTIAL_ADSERVING = 0x01;
    public static final int DELIVERY_SCHEDULE = 0x08;
    public static final int OPTIMIZE_CREATIVE_WEIGHT = 0x20;
    public static final int LINKED_TO_CAMPAIGN_END_DATE = 0x100;
    public static final int INCLUDE_SPECIFIC_PUBL = 0x200;
    public static final int ISP_COLOCATION_TARGETING = 0x02;

    public static final int EXTERNAL_MASK =
              DELIVERY_SCHEDULE
            | LINKED_TO_CAMPAIGN_END_DATE
            | OPTIMIZE_CREATIVE_WEIGHT
            | SEQUENTIAL_ADSERVING;

    public static final int INTERNAL_MASK =
              INCLUDE_SPECIFIC_PUBL
            | ISP_COLOCATION_TARGETING;

    private static final Flags DEFAULT_FLAGS = new Flags(OPTIMIZE_CREATIVE_WEIGHT);

    @RequiredConstraint
    @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID", updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Campaign campaign;

    @GenericGenerator(name = "CampaignCreativeGroupGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "CAMPAIGNCREATIVEGROUP_CCG_ID_SEQ")})
    @Id
    @GeneratedValue(generator = "CampaignCreativeGroupGen")
    @Column(name = "CCG_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @RequiredConstraint
    @NoSecondsConstraint
    @Column(name = "DATE_START")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateStart;

    @NoSecondsConstraint
    @Column(name = "DATE_END")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnd;

    @Column(name = "FLAGS", nullable = false)
    @Type(type = "com.foros.persistence.hibernate.type.FlagsType")
    private Flags flags = DEFAULT_FLAGS;

    @Column(name = "DAILY_IMP", nullable = false, updatable = false, insertable = false)
    private long dailyImpressions;

    @Column(name = "BUDGET")
    private BigDecimal budget;

    @Column(name = "DAILY_BUDGET", nullable = true)
    private BigDecimal dailyBudget;

    @HasIdConstraint
    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Country country;

    @JoinTable(name = "CCGSITE",
        joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
        inverseJoinColumns = {@JoinColumn(name = "SITE_ID", referencedColumnName = "SITE_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Site> sites = new LinkedHashSet<Site>();

    @JoinTable(name = "CCGCOLOCATION",
            joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "COLO_ID", referencedColumnName = "COLO_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Colocation> colocations = new LinkedHashSet<Colocation>();

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "creativeGroup", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<CampaignCreative> campaignCreatives = new LinkedHashSet<CampaignCreative>();

    @Column(name = "CCG_TYPE", updatable = false)
    private char ccgType;

    @Column(name = "TGT_TYPE", updatable = false)
    private char tgtType;

    @JoinColumn(name = "FREQ_CAP_ID", referencedColumnName = "FREQ_CAP_ID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private FrequencyCap frequencyCap;

    @RequiredConstraint
    @JoinColumn(name = "CCG_RATE_ID", referencedColumnName = "CCG_RATE_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private CcgRate ccgRate;

    @RequiredConstraint
    @Column(name = "BID_STRATEGY", nullable = false)
    private char bidStrategy = BidStrategy.MAXIMISE_REACH.getLetter();

    @RequiredConstraint
    @Column(name = "MIN_CTR_GOAL", nullable = false)
    private BigDecimal minCtrGoal = BigDecimal.ZERO;

    @JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Channel channel;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "creativeGroup", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<CCGKeyword> ccgKeywords = new LinkedHashSet<CCGKeyword>();

    @JoinTable(name = "CCGACTION",
        joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
        inverseJoinColumns = {@JoinColumn(name = "ACTION_ID", referencedColumnName = "ACTION_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Action> actions = new LinkedHashSet<Action>();

    @JoinTable(name = "CCGGeoChannel",
            joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GEO_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<GeoChannel> geoChannels = new LinkedHashSet<GeoChannel>();

    @JoinTable(name = "CCGGeoChannelExcluded",
            joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GEO_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<GeoChannel> geoChannelsExcluded = new LinkedHashSet<GeoChannel>();

    @Column(name = "CHANNEL_TARGET", nullable = false)
    private char channelTarget;

    @RequiredConstraint
    @Column(name = "DELIVERY_PACING", nullable = false)
    private char deliveryPacing = DeliveryPacing.UNRESTRICTED.getLetter();

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "campaignCreativeGroup", fetch =  FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    // @OrderBy("timeFrom ASC") becomes first order by condition in com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl
    private Set<CCGSchedule> ccgSchedules = new LinkedHashSet<CCGSchedule>();

    @Column(name = "USER_SAMPLE_GROUP_START")
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    @RangeConstraint(min = "1", max = "100")
    private Long userSampleGroupStart;

    @Column(name = "USER_SAMPLE_GROUP_END")
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    @RangeConstraint(min = "1", max = "100")
    private Long userSampleGroupEnd;

    @Column(name = "OPTIN_STATUS_TARGETING")
    @Type(type="com.foros.persistence.hibernate.type.OptInStatusTargetingType")
    @CopyPolicy(strategy = CopyStrategy.DEEP)
    private OptInStatusTargeting optInStatusTargeting;

    @Column(name = "MIN_UID_AGE")
    @RangeConstraint(min = "0", max = "10000")
    private Long minUidAge;

    @JoinTable(name = "CCGDeviceChannel",
            joinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")},
            inverseJoinColumns = {@JoinColumn(name = "DEVICE_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<DeviceChannel> deviceChannels = new LinkedHashSet<DeviceChannel>();

    @Column(name = "CHECK_INTERVAL_NUM")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Integer interval;

    @Column(name = "LAST_CHECK_DATE")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Date lastCheckDate;

    @Column(name = "NEXT_CHECK_DATE")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Date nextCheckDate;

    @JoinColumn(referencedColumnName = "USER_ID", name = "CHECK_USER_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private User checkUser;

    @Column(name = "CHECK_NOTES")
    @StringSizeConstraint(size = 4000)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private String checkNotes;

    @Column(name = "CTR_RESET_ID")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long ctrResetId = 0L;

    @Column(name = "ROTATION_CRITERIA")
    @RangeConstraint(min = "1", max = "999999999")
    private Long rotationCriteria;

    @Column(name = "TARGETING_CHANNEL_ID", updatable = false, insertable = false)
    private Long targetingChannelId;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "creativegroup.displaystatus.live");
    public static final DisplayStatus LIVE_LINKED_CREATIVE_NEED_ATT = new DisplayStatus(2L, DisplayStatus.Major.LIVE_NEED_ATT, "creativegroup.displaystatus.live_linked_creatives_n_a");
    public static final DisplayStatus LIVE_KEYWORDS_NEED_ATT = new DisplayStatus(3L, DisplayStatus.Major.LIVE_NEED_ATT, "creativegroup.displaystatus.live_keywords_n_a");
    public static final DisplayStatus LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT = new DisplayStatus(4L, DisplayStatus.Major.LIVE_NEED_ATT, "creativegroup.displaystatus.live_linked_creatives_keywords_n_a");
    public static final DisplayStatus DATE_NOT_IN_RANGE = new DisplayStatus(5L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.date_not_in_range");
    public static final DisplayStatus NO_AVAILABLE_BUDGET = new DisplayStatus(6L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.no_available_budget");
    public static final DisplayStatus NOT_LIVE_LINKED_CREATIVE_NEED_ATT = new DisplayStatus(7L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.not_live_linked_creatives_n_a");
    public static final DisplayStatus NOT_LIVE_KW_NEED_ATT = new DisplayStatus(8L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.not_live_keywords_n_a");
    public static final DisplayStatus NOT_LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT = new DisplayStatus(9L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.not_live_linked_creatives_keywords_n_a");
    public static final DisplayStatus NOT_LIVE_CHANNEL_TARGET_NEED_ATT = new DisplayStatus(10L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.not_live_channel_target_n_a");
    public static final DisplayStatus DECLINED = new DisplayStatus(11L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.declined");
    public static final DisplayStatus PENDING_FOROS = new DisplayStatus(12L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.pending_foros");
    public static final DisplayStatus PENDING_USER = new DisplayStatus(13L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.pending_user");
    public static final DisplayStatus INACTIVE = new DisplayStatus(14L, DisplayStatus.Major.INACTIVE, "creativegroup.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(15L, DisplayStatus.Major.DELETED, "creativegroup.displaystatus.deleted");
    public static final DisplayStatus GOAL_REACHED = new DisplayStatus(16L, DisplayStatus.Major.NOT_LIVE, "creativegroup.displaystatus.goal_reached");
    public static final DisplayStatus LIVE_CHANNEL_TARGET_NEED_ATT = new DisplayStatus(17L, DisplayStatus.Major.LIVE_NEED_ATT, "creativegroup.displaystatus.live_channel_target_n_a");

    public static Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
            LIVE,
            LIVE_LINKED_CREATIVE_NEED_ATT,
            LIVE_KEYWORDS_NEED_ATT,
            LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT,
            DATE_NOT_IN_RANGE,
            NO_AVAILABLE_BUDGET,
            NOT_LIVE_LINKED_CREATIVE_NEED_ATT,
            NOT_LIVE_KW_NEED_ATT,
            NOT_LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT,
            NOT_LIVE_CHANNEL_TARGET_NEED_ATT,
            DECLINED,
            PENDING_FOROS,
            PENDING_USER,
            INACTIVE,
            DELETED,
            GOAL_REACHED,
            LIVE_CHANNEL_TARGET_NEED_ATT
    );

    static public DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(displayStatusId);
    }

    static public Collection<DisplayStatus> getAvailableDisplayStatuses() {
        return displayStatusMap.values();
    }

    public static DisplayStatus getDisplayStatusPA_User() {
        return PENDING_USER;
    }

    public CampaignCreativeGroup() {
    }

    public CampaignCreativeGroup(Long id) {
        this.id = id;
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
        return isLinkedToCampaignEndDateFlag() ? null : dateEnd;
    }

    public Date getEffectiveDateEnd() {
        return isLinkedToCampaignEndDateFlag() ? getCampaign().getDateEnd() : dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
        this.registerChange("dateEnd");
    }

    @XmlTransient
    public long getFlags() {
        return flags.intValue();
    }

    @XmlTransient
    public Flags getFlagsObject() {
        return flags;
    }

    private void setFlagsInternal(Flags f) {
        this.flags = f;
        this.registerChange("flags");
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    @XmlElementWrapper(name = "sites")
    @XmlElement(name = "site")
    @XmlJavaTypeAdapter(SiteLinkXmlAdapter.class)
    public Set<Site> getSites() {
        return ChangesSupportSet.wrap(this, "sites", sites);
    }

    public void setSites(Set<Site> sites) {
        this.sites = sites;
        this.registerChange("sites");
    }

    @XmlElementWrapper(name = "colocations")
    @XmlElement(name = "colocation")
    @XmlJavaTypeAdapter(ColocationLinkXmlAdapter.class)
    public Set<Colocation> getColocations() {
        return ChangesSupportSet.wrap(this, "colocations", colocations);
    }

    public void setColocations(Set<Colocation> colocations) {
        this.colocations = colocations;
        this.registerChange("colocations");
    }

    @XmlTransient
    public Set<CampaignCreative> getCampaignCreatives() {
        return new ChangesSupportSet<CampaignCreative>(this, "campaignCreatives", campaignCreatives);
    }

    public void setCampaignCreatives(Set<CampaignCreative> campaignCreatives) {
        this.campaignCreatives = campaignCreatives;
        this.registerChange("campaignCreatives");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CampaignXmlAdapter.class)
    public Campaign getCampaign() {
        return this.campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        this.registerChange("campaign");
    }

    @Override
    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    @Override
    public void setFrequencyCap(FrequencyCap frequencyCap) {
        this.frequencyCap = frequencyCap;
        this.registerChange("frequencyCap");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CcgRateXmlAdapter.class)
    public CcgRate getCcgRate() {
        return ccgRate;
    }

    public void setCcgRate(CcgRate ccgRate) {
        if (ccgRate != null) ccgRate.setCcg(this);
        this.ccgRate = ccgRate;
        this.registerChange("ccgRate");
    }

    public BidStrategy getBidStrategy() {
        return BidStrategy.valueOf(bidStrategy);
    }

    public void setBidStrategy(BidStrategy bidStrategy) {
        this.bidStrategy = bidStrategy.getLetter();
        this.registerChange("bidStrategy");
    }

    public BigDecimal getMinCtrGoal() {
        return minCtrGoal;
    }

    public void setMinCtrGoal(BigDecimal minCtrGoal) {
        this.minCtrGoal = minCtrGoal;
        this.registerChange("minCtrGoal");
    }

    @XmlJavaTypeAdapter(ChannelLinkXmlAdapter.class)
    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        this.registerChange("channel");
    }

    @XmlTransient
    public long getDailyImpressions() {
        return dailyImpressions;
    }

    public void setDailyImpressions(long dailyImpressions) {
        this.dailyImpressions = dailyImpressions;
        this.registerChange("dailyImpressions");
    }

    @Override
    @XmlTransient
    public AdvertiserAccount getAccount() {
        return campaign == null ? null : campaign.getAccount();
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
        this.registerChange("budget");
    }

    public CCGType getCcgType() {
        return CCGType.valueOf(ccgType);
    }

    public void setCcgType(CCGType ccgType) {
        this.ccgType = ccgType.getLetter();
        this.registerChange("ccgType");
    }

    public TGTType getTgtType() {
        return TGTType.valueOf(tgtType);
    }

    public void setTgtType(TGTType tgtType) {
        this.tgtType = tgtType.getLetter();
        this.registerChange("tgtType");
    }

    @XmlTransient
    public Set<CCGKeyword> getCcgKeywords() {
        return new ChangesSupportSet<CCGKeyword>(this, "ccgKeywords", ccgKeywords);
    }

    public void setCcgKeywords(Set<CCGKeyword> ccgKeywords) {
        this.ccgKeywords = ccgKeywords;
        this.registerChange("ccgKeywords");
    }

    @XmlElementWrapper(name = "conversions")
    @XmlElement(name = "conversion")
    @XmlJavaTypeAdapter(ActionLinkXmlAdapter.class)
    public Set<Action> getActions() {
        return new ChangesSupportSet<Action>(this, "actions", actions);
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
        this.registerChange("actions");
    }

    @Override
    @XmlElementWrapper(name = "deviceChannels")
    @XmlElement(name = "deviceChannel")
    @XmlJavaTypeAdapter(DeviceChannelLinkXmlAdapter.class)
    public Set<DeviceChannel> getDeviceChannels() {
        return new ChangesSupportSet<DeviceChannel>(this, "deviceChannels", deviceChannels);
    }

    public void setDeviceChannels(Set<DeviceChannel> deviceChannels) {
        this.deviceChannels = deviceChannels;
        this.registerChange("deviceChannels");
    }

    @XmlElementWrapper(name = "geoChannels")
    @XmlElement(name = "geoChannel")
    @XmlJavaTypeAdapter(GeoChannelLinkXmlAdapter.class)
    public Set<GeoChannel> getGeoChannels() {
        return new ChangesSupportSet<GeoChannel>(this, "geoChannels", geoChannels);
    }

    public void setGeoChannels(Set<GeoChannel> geoChannels) {
        this.geoChannels = geoChannels;
        this.registerChange("geoChannels");
    }

    @XmlElementWrapper(name = "geoChannelsExcluded")
    @XmlElement(name = "geoChannel")
    @XmlJavaTypeAdapter(GeoChannelLinkXmlAdapter.class)
    public Set<GeoChannel> getGeoChannelsExcluded() {
        return new ChangesSupportSet<GeoChannel>(this, "geoChannelsExcluded", geoChannelsExcluded);
    }

    public void setGeoChannelsExcluded(Set<GeoChannel> geoChannelsExcluded) {
        this.geoChannelsExcluded = geoChannelsExcluded;
        this.registerChange("geoChannelsExcluded");
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
        this.registerChange("dailyBudget");
    }

    public ChannelTarget getChannelTarget() {
        return ChannelTarget.valueOf(channelTarget);
    }

    public void setChannelTarget(ChannelTarget channelTarget) {
        this.channelTarget = channelTarget != null ? channelTarget.getLetter() : '\u0000';
        this.registerChange("channelTarget");
    }

    public Boolean isLinkedToCampaignEndDateFlag() {
        return flags.get(LINKED_TO_CAMPAIGN_END_DATE);
    }

    public void setLinkedToCampaignEndDateFlag(Boolean flag) {
        setFlagsInternal(flags.set(LINKED_TO_CAMPAIGN_END_DATE, flag));
        registerChange("linkedToCampaignEndDateFlag");
    }

    public DeliveryPacing getDeliveryPacing() {
        return DeliveryPacing.valueOf(deliveryPacing);
    }

    public void setDeliveryPacing(DeliveryPacing deliveryPacing) {
        this.deliveryPacing = deliveryPacing.getLetter();
        this.registerChange("deliveryPacing");
    }

    @XmlTransient
    public boolean isOptimizeCreativeWeightFlag() {
        return flags.get(OPTIMIZE_CREATIVE_WEIGHT);
    }

    public void setOptimizeCreativeWeightFlag(boolean value) {
        setFlagsInternal(flags.set(OPTIMIZE_CREATIVE_WEIGHT, value));
        registerChange("optimizeCreativeWeightFlag");
    }

    @XmlTransient
    public boolean isSequentialAdservingFlag() {
        return flags.get(SEQUENTIAL_ADSERVING);
    }

    public void setSequentialAdservingFlag(boolean value) {
        setFlagsInternal(flags.set(SEQUENTIAL_ADSERVING, value));
        registerChange("sequentialAdservingFlag");
    }

    @XmlTransient
    public boolean isIncludeSpecificSitesFlag() {
        return flags.get(INCLUDE_SPECIFIC_PUBL);
    }

    public void setIncludeSpecificSitesFlag(boolean value) {
        setFlagsInternal(flags.set(INCLUDE_SPECIFIC_PUBL, value));
        registerChange("includeSpecificSites");
    }

    @XmlTransient
    public boolean isIspColocationTargetingFlag() {
        return flags.get(ISP_COLOCATION_TARGETING);
    }

    public void setIspColocationTargetingFlag(boolean value) {
        setFlagsInternal(flags.set(ISP_COLOCATION_TARGETING, value));
        registerChange("ispColocationTargeting");
    }

    @Override
     public Status getParentStatus() {
        return campaign.getInheritedStatus();
    }

    @Override
    public String toString() {
        return "com.foros.model.campaign.CampaignCreativeGroup[id=" + getId() + "]";
    }

    public Date getCalculatedStartDate() {
        // for group Start Date system uses max(Campaign Start Date, Group Start Date)
        if (dateStart != null && getCampaign().getDateStart().after(dateStart)) {
            return getCampaign().getDateStart();
        }
        return dateStart;
    }

    /**
     * Calculation of End date incorporates logic for calculating the endDate of creative group if it has
     * "linked to campaign endDate" flag set.
     *
     * @return if "linked to campaign endDate" returns campaign.endDate.
     */
    public Date getCalculatedEndDate() {
        Date campaignEndDate = getCampaign().getDateEnd();

        if (!isLinkedToCampaignEndDateFlag()) {
            // ccg end date not linked to campaign end date
            if (campaignEndDate == null) {
                // campaign end date is set, use this date
                return this.dateEnd;
            }

            if (this.dateEnd != null && campaignEndDate.before(this.dateEnd)) {
                // for group End Date system uses min(Campaign End Date, Group End Date)
                return campaignEndDate;
            } else {
                return this.dateEnd;
            }
        }

        return campaignEndDate;
    }

    @XmlElementWrapper(name = "ccgSchedules")
    @XmlElement(name = "ccgSchedule")
    public Set<CCGSchedule> getCcgSchedules() {
        return new ChangesSupportSet<CCGSchedule>(this, "ccgSchedules", ccgSchedules);
    }

    public void setCcgSchedules(Set<CCGSchedule> ccgSchedules) {
        this.ccgSchedules = ccgSchedules;
        this.registerChange("ccgSchedules");
    }

    public Boolean isDeliveryScheduleFlag() {
        return flags.get(DELIVERY_SCHEDULE);
    }

    public void setDeliveryScheduleFlag(Boolean flag) {
        setFlagsInternal(flags.set(DELIVERY_SCHEDULE, flag));
        registerChange("deliveryScheduleFlag");
    }
    @XmlTransient
    public Long getUserSampleGroupEnd() {
        return userSampleGroupEnd;
    }

    @XmlTransient
    public Long getUserSampleGroupStart() {
        return userSampleGroupStart;
    }

    public void setUserSampleGroupEnd(Long userSampleGroupEnd) {
        this.userSampleGroupEnd = userSampleGroupEnd;
        this.registerChange("userSampleGroupEnd");
    }

    public void setUserSampleGroupStart(Long userSampleGroupStart) {
        this.userSampleGroupStart = userSampleGroupStart;
        this.registerChange("userSampleGroupStart");
    }

    @XmlElement
    @XmlJavaTypeAdapter(OptInStatusTargetingXmlAdapter.class)
    public OptInStatusTargeting getOptInStatusTargeting() {
        return optInStatusTargeting;
    }

    public void setOptInStatusTargeting(OptInStatusTargeting optInStatusTargeting) {
        this.optInStatusTargeting = optInStatusTargeting;
        this.registerChange("optInStatusTargeting");
    }

    public Long getMinUidAge() {
        return minUidAge;
    }

    public void setMinUidAge(Long minUidAge) {
        this.minUidAge = minUidAge;
        this.registerChange("minUidAge");
    }

    @Override
    @XmlTransient
    public Integer getInterval() {
        return interval;
    }

    @Override
    public void setInterval(Integer interval) {
        this.interval = interval;
        this.registerChange("interval");
    }

    @Override
    @XmlTransient
    public Date getLastCheckDate() {
        return lastCheckDate;
    }

    @Override
    public void setLastCheckDate(Date lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
        this.registerChange("lastCheckDate");
    }

    @Override
    @XmlTransient
    public Date getNextCheckDate() {
        return nextCheckDate;
    }

    @Override
    public void setNextCheckDate(Date nextCheckDate) {
        this.nextCheckDate = nextCheckDate;
        this.registerChange("nextCheckDate");
    }

    @Override
    @XmlTransient
    public User getCheckUser() {
        return checkUser;
    }

    @Override
    public void setCheckUser(User checkUser) {
        this.checkUser = checkUser;
        this.registerChange("checkUser");
    }

    @Override
    @XmlTransient
    public String getCheckNotes() {
        return checkNotes;
    }

    @Override
    public void setCheckNotes(String checkNotes) {
        this.checkNotes = checkNotes;
        this.registerChange("checkNotes");
    }

    @XmlTransient
    public Long getCtrResetId() {
        return ctrResetId;
    }

    public void setCtrResetId(Long ctrResetId) {
        this.ctrResetId = ctrResetId;
        this.registerChange("ctrResetId");
    }

    @XmlTransient
    public Long getRotationCriteria() {
        return rotationCriteria;
    }

    public void setRotationCriteria(Long rotationCriteria) {
        this.rotationCriteria = rotationCriteria;
        this.registerChange("rotationCriteria");
    }

    public Long getTargetingChannelId() {
        return targetingChannelId;
    }
}
