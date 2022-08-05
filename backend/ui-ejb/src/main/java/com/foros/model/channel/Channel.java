package com.foros.model.channel;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.cache.generic.interceptor.annotations.Evictable;
import com.foros.jaxb.adapters.AccountXmlAdapter;
import com.foros.jaxb.adapters.ChannelLinkXmlAdapter;
import com.foros.jaxb.adapters.ChannelRateXmlAdapter;
import com.foros.jaxb.adapters.DateTimeXmlAdapter;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.IdNameEntity;
import com.foros.model.OwnedApprovable;
import com.foros.model.RegularCheckable;
import com.foros.model.Status;
import com.foros.model.StatusChangeDateAware;
import com.foros.model.account.Account;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.security.User;
import com.foros.session.channel.ExpressionChannelFormatter;
import com.foros.util.PersistenceUtils;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.expression.ExpressionHelper;
import com.foros.validation.constraint.ExpressionSymbolsOnlyConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SingleLineConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.XmlAllowableConstraint;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Evictable
@Entity
@Table(name = "CHANNEL")
@Inheritance
@DiscriminatorColumn(name = "CHANNEL_TYPE")
@AllowedQAStatuses(values = { ApproveStatus.APPROVED, ApproveStatus.DECLINED })
@NamedQueries({
        @NamedQuery(name = "Channel.findByAccountIdAndName",
                query = "SELECT c FROM Channel c WHERE c.account.id = :accountId AND c.name = :channelName"),
        @NamedQuery(name = "Channel.getAdvertisingChannelsByAccount",
                query="SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Channel c " +
                        "  WHERE c.account.id=:accountId and" +
                        "  (c.namespace = com.foros.model.channel.ChannelNamespace.ADVERTISING or c.namespace = com.foros.model.channel.ChannelNamespace.KEYWORD)"),
        @NamedQuery(name = "Channel.getNonDeletedChannelsByAccount",
                query = "SELECT NEW com.foros.session.channel.ChannelTO(c.id, c.name, c.status, c.qaStatus, c.displayStatusId, co.countryCode, c.visibility, c.class) FROM Channel c" +
                        " LEFT JOIN c.country co " +
                        " WHERE c.account.id = :accountId" +
                        " AND c.status <>'D'" +
                        " ORDER BY upper(c.name)"),
        @NamedQuery(name = "Channel.getAllChannelsByAccount",
                query = "SELECT NEW com.foros.session.channel.ChannelTO(c.id, c.name, c.status, c.qaStatus, c.displayStatusId, co.countryCode, c.visibility, c.class) FROM Channel c" +
                        " LEFT JOIN c.country co " +
                        " WHERE c.account.id = :accountId" +
                        " ORDER BY upper(c.name)")
})
@XmlSeeAlso({ExpressionChannel.class, BehavioralChannel.class, AudienceChannel.class, DiscoverChannel.class})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlType(propOrder = {
        "country",
        "id",
        "account",
        "name",
        "statusChangeDate",
        "visibility",
        "supersededByChannel",
        "description",
        "channelRate"
})
public abstract class Channel extends ApprovableEntity implements OwnedApprovable<Account>, IdNameEntity, StatusChangeDateAware, RegularCheckable {
    public static final String CHANNEL_TYPE_BEHAVIORAL = "B";
    public static final String CHANNEL_TYPE_DISCOVER = "D";
    public static final String CHANNEL_TYPE_EXPRESSION = "E";
    public static final String CHANNEL_TYPE_CATEGORY = "C";
    public static final String CHANNEL_TYPE_DISCOVER_CHANNEL_LIST = "L";
    public static final String CHANNEL_TYPE_KEYWORD = "K";
    public static final String CHANNEL_TYPE_GEO = "G";
    public static final String CHANNEL_TYPE_DEVICE = "V";
    public static final String CHANNEL_TYPE_SPECIAL = "S";
    public static final String CHANNEL_TYPE_AUDIENCE = "A";
    public static final String CHANNEL_TYPE_PLACEMENT_BLACKLIST = "P";

    @GenericGenerator(name = "ChannelGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "CHANNEL_CHANNEL_ID_SEQ")})
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChannelGen")
    @Id
    @Column(name = "CHANNEL_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @IdConstraint
    private Long id;

    @RequiredConstraint
    @SingleLineConstraint
    @XmlAllowableConstraint
    @ExpressionSymbolsOnlyConstraint
    @Column(name = "NAME")
    private String name;

    @Column(name = "FLAGS", nullable = false)
    private long flags;

    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Account account;

    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Country country;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @Cascade(CascadeType.DETACH)
    private Set<CampaignCreativeGroup> campaignCreativeGroups = new LinkedHashSet<CampaignCreativeGroup>(0);

    @Column(name = "VISIBILITY")
    @Enumerated(EnumType.STRING)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private ChannelVisibility visibility = ChannelVisibility.PRI;

    @Column(name = "DESCRIPTION")
    @StringSizeConstraint(size = 2000)
    private String description;

    @Column(name = "STATUS_CHANGE_DATE")
    private Date statusChangeDate;

    @JoinColumn(name = "SUPERSEDED_BY_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Channel supersededByChannel;

    @JoinColumn(name = "CHANNEL_RATE_ID", referencedColumnName = "CHANNEL_RATE_ID")
    @ManyToOne(cascade = {})
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    @ChangesInspection(type = InspectionType.CASCADE)
    private ChannelRate channelRate;

    @Column(name = "NAMESPACE", nullable = false, updatable = false)
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.channel.ChannelNamespace"),
            @Parameter(name = "identifierMethod", value = "getLetter"),
            @Parameter(name = "valueOfMethod", value = "byLetter") })
    @ChangesInspection(type = InspectionType.NONE)
    private ChannelNamespace namespace = calculateNamespace();

    @JoinTable(name = "CHANNELCATEGORY",
        joinColumns = {@JoinColumn(name = "CHANNEL_ID", referencedColumnName = "CHANNEL_ID")},
        inverseJoinColumns = {@JoinColumn(name = "CATEGORY_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<CategoryChannel> categories = new HashSet<CategoryChannel>();

    @Column(name = "MESSAGE_SENT")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private int messageSent;

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

    // actually it should be abstract but hibernate crashes in this case
    protected ChannelNamespace calculateNamespace() {
        return null;
    }

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "channel.displaystatus.live");
    public static final DisplayStatus DECLINED = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "channel.displaystatus.declined");
    public static final DisplayStatus PENDING_FOROS = new DisplayStatus(3L, DisplayStatus.Major.NOT_LIVE, "channel.displaystatus.pending_foros");
    public static final DisplayStatus INACTIVE = new DisplayStatus(4L, DisplayStatus.Major.INACTIVE, "channel.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(5L, DisplayStatus.Major.DELETED, "channel.displaystatus.deleted");
    public static final DisplayStatus LIVE_PENDING_INACTIVATION = new DisplayStatus(6L, DisplayStatus.Major.LIVE, "channel.displaystatus.live_pending_inactivation");
    public static final DisplayStatus NOT_LIVE_CHANNELS_NEED_ATT = new DisplayStatus(7L, DisplayStatus.Major.NOT_LIVE, "channel.displaystatus.not_live_channels_na");
    public static final DisplayStatus NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS = new DisplayStatus(8L, DisplayStatus.Major.NOT_LIVE, "channel.displaystatus.not_live_not_enough_users","channel.displaystatus.not_live_not_enough_users.external");
    public static final DisplayStatus LIVE_CHANNELS_NEED_ATT = new DisplayStatus(9L, DisplayStatus.Major.LIVE_NEED_ATT, "channel.displaystatus.live_channels_na");
    public static final DisplayStatus LIVE_TRIGGERS_NEED_ATT = new DisplayStatus(10L, DisplayStatus.Major.LIVE_NEED_ATT, "channel.displaystatus.live_triggers_na");
    public static final DisplayStatus LIVE_AMBER_PENDING_INACTIVATION = new DisplayStatus(11L, DisplayStatus.Major.LIVE_NEED_ATT, "channel.displaystatus.live_pending_inactivation_na");

    public static final Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        DECLINED,
        PENDING_FOROS,
        INACTIVE,
        DELETED,
        LIVE_PENDING_INACTIVATION,
        NOT_LIVE_CHANNELS_NEED_ATT,
        NOT_LIVE_NOT_ENOUGH_UNIQUE_USERS,
        LIVE_CHANNELS_NEED_ATT,
        LIVE_TRIGGERS_NEED_ATT,
        LIVE_AMBER_PENDING_INACTIVATION
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

    public static DisplayStatus getDisplayStatusPA_FOROS() {
        return PENDING_FOROS;
    }

    public Channel() {
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getFullName() {
        ExpressionChannelFormatter formatter = ExpressionHelper.newChannelNameFormatter();
        return formatter.format(this);
    }

    @XmlTransient
    public long getFlags() {
        return this.flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(AccountXmlAdapter.class)
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        this.registerChange("account");
    }

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || !PersistenceUtils.isSameClass(this, o)) {
            return false;
        }

        Channel channel = (Channel) o;


        if (id != null) {
            return id.equals(channel.id);
        }

        if (channel.id != null) {
            return false;
        }

        if (name != null ? !name.equals(channel.name) : channel.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return 31 * (name != null ? name.hashCode() : 0);
        }
    }

    @XmlTransient
    public Set<CampaignCreativeGroup> getCampaignCreativeGroups() {
        return new ChangesSupportSet<CampaignCreativeGroup>(this, "campaignCreativeGroups", campaignCreativeGroups);
    }

    public void setCampaignCreativeGroups(Set<CampaignCreativeGroup> campaignCreativeGroups) {
        this.campaignCreativeGroups = campaignCreativeGroups;
        this.registerChange("campaignCreativeGroups");
    }

    public ChannelVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ChannelVisibility visibility) {
        this.visibility = visibility;
        this.registerChange("visibility");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.registerChange("description");
    }

    @Override
    @XmlJavaTypeAdapter(DateTimeXmlAdapter.class)
    public Date getStatusChangeDate() {
        return statusChangeDate;
    }

    @Override
    public void setStatusChangeDate(Date statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
        this.registerChange("statusChangeDate");
    }

    public void setSupersededByChannel(Channel supersededByChannel) {
        this.supersededByChannel = supersededByChannel;
        this.registerChange("supersededByChannel");
    }

    @XmlJavaTypeAdapter(ChannelLinkXmlAdapter.class)
    public Channel getSupersededByChannel() {
        return supersededByChannel;
    }

    @XmlElement
    @XmlJavaTypeAdapter(ChannelRateXmlAdapter.class)
    public ChannelRate getChannelRate() {
        return channelRate;
    }

    public void setChannelRate(ChannelRate channelRate) {
        this.channelRate = channelRate;
        this.registerChange("channelRate");
    }

    @Override
    public Status getParentStatus() {
        return account.getInheritedStatus();
    }

    @XmlTransient
    public Set<CategoryChannel> getCategories() {
        return ChangesSupportSet.wrap(this, "categories", categories);
    }

    public void setCategories(Set<CategoryChannel> categories) {
        this.categories = categories;
        this.registerChange("categories");
    }

    @XmlTransient
    public int getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(int messageSent) {
        this.messageSent = messageSent;
        this.registerChange("messageSent");
    }

    @XmlTransient
    public ChannelNamespace getNamespace() {
        return namespace;
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
    public abstract String getChannelType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id: " + id + " name: " + name + "]";
    }
}
