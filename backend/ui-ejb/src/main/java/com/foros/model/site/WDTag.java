package com.foros.model.site;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.PublisherAccount;
import com.foros.model.feed.Feed;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.template.DiscoverTemplate;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.UrlConstraint;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "WDTAG")
@NamedQueries({
    @NamedQuery(name = "WDTag.getIndexBySite", query = "" +
        "SELECT NEW com.foros.session.site.WDTagTO(wdtag.id,wdtag.name,wdtag.status,wdtag.width,wdtag.height) " +
        " FROM WDTag wdtag WHERE wdtag.site.id = :siteId order by upper(wdtag.name)"
    ),
    @NamedQuery(name = "WDTag.getNonDeletedIndexBySite", query = "" +
        "SELECT NEW com.foros.session.site.WDTagTO(wdtag.id,wdtag.name,wdtag.status,wdtag.width,wdtag.height) " +
        " FROM WDTag wdtag WHERE wdtag.site.id = :siteId and wdtag.status <> 'D' order by upper(wdtag.name)"
    )
})
@AllowedStatuses(values = { Status.ACTIVE, Status.DELETED })
public class WDTag extends StatusEntityBase implements Serializable, OwnedStatusable<PublisherAccount>, DisplayStatusEntity, IdNameEntity {
    public enum FeedOption {
        A, S, P;

        public char getLetter() {
            return name().charAt(0);
        }

        public static FeedOption valueOf(char letter) throws IllegalArgumentException {
            return valueOf(String.valueOf(letter));
        }
    }

    @SequenceGenerator(name = "WDTagGen", sequenceName = "WDTAG_WDTAG_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WDTagGen")
    @Column(name = "WDTAG_ID", nullable = false)
    @IdConstraint
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNullConstraint
    @JoinColumn(name = "SITE_ID", referencedColumnName = "SITE_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Site site;

    @RequiredConstraint
    @RangeConstraint(min = "1", max = "2000")
    @Column(name = "WIDTH")
    private Long width;

    @RequiredConstraint
    @RangeConstraint(min = "1", max = "2000")
    @Column(name = "HEIGHT")
    private Long height;

    @Column(name = "OPTED_IN_CONTENT")
    @Enumerated(EnumType.STRING)
    private FeedOption optedInOption = FeedOption.A;

    @JoinTable(name = "WDTAGFEED_OPTEDIN",
        joinColumns = {@JoinColumn(name = "WDTAG_ID", referencedColumnName = "WDTAG_ID")},
        inverseJoinColumns = {@JoinColumn(name = "FEED_ID", referencedColumnName = "FEED_ID")}
    )
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Feed> optedInFeeds = new HashSet<Feed>();

    @Column(name = "OPTED_OUT_CONTENT")
    @Enumerated(EnumType.STRING)
    private FeedOption optedOutOption = FeedOption.A;

    @JoinTable(name = "WDTAGFEED_OPTEDOUT",
        joinColumns = {@JoinColumn(name = "WDTAG_ID", referencedColumnName = "WDTAG_ID")},
        inverseJoinColumns = {@JoinColumn(name = "FEED_ID", referencedColumnName = "FEED_ID")}
    )
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<Feed> optedOutFeeds = new HashSet<Feed>();

    @UrlConstraint
    @StringSizeConstraint(size = 2000)
    @Column(name = "PASSBACK")
    private String passbackUrl;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private DiscoverTemplate template;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag", fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<WDTagOptionValue> options = new LinkedHashSet<WDTagOptionValue>();

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "tag", fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<WDTagOptGroupState> groupStates = new LinkedHashSet<WDTagOptGroupState>();

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "wdtag.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "wdtag.displaystatus.deleted");

    public WDTag() {
    }

    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(getStatus());
    }

    public static DisplayStatus getDisplayStatus(Status status) {
        if (Status.ACTIVE.equals(status)) {
            return LIVE;
        } else if (Status.DELETED.equals(status)){
            return DELETED;
        } else {
            return null;
        }
    }

    @Override
    public Long getId() {
        return id;
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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
        this.registerChange("site");
    }

    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
        this.registerChange("width");
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
        this.registerChange("height");
    }

    public FeedOption getOptedInOption() {
        return optedInOption;
    }

    public void setOptedInOption(FeedOption optedInOption) {
        this.optedInOption = optedInOption;
        this.registerChange("optedInOption");
    }

    public Set<Feed> getOptedInFeeds() {
        return new ChangesSupportSet<Feed>(this, "optedInFeeds", optedInFeeds);
    }

    public void setOptedInFeeds(Set<Feed> optedInFeeds) {
        this.optedInFeeds = optedInFeeds;
        this.registerChange("optedInFeeds");
    }

    public FeedOption getOptedOutOption() {
        return optedOutOption;
    }

    public void setOptedOutOption(FeedOption optedOutOption) {
        this.optedOutOption = optedOutOption;
        this.registerChange("optedOutOption");
    }

    public Set<Feed> getOptedOutFeeds() {
        return new ChangesSupportSet<Feed>(this, "optedOutFeeds", optedOutFeeds);
    }

    public void setOptedOutFeeds(Set<Feed> optedOutFeeds) {
        this.optedOutFeeds = optedOutFeeds;
        this.registerChange("optedOutFeeds");
    }

    public String getPassbackUrl() {
        return passbackUrl;
    }

    public void setPassbackUrl(String passbackUrl) {
        this.passbackUrl = passbackUrl;
        this.registerChange("passbackUrl");
    }

    public DiscoverTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DiscoverTemplate template) {
        this.template = template;
        this.registerChange("template");
    }

    public Set<WDTagOptionValue> getOptions() {
        return new ChangesSupportSet<WDTagOptionValue>(this, "options", options);
    }

    public void setOptions(Set<WDTagOptionValue> options) {
        this.options = options;
        this.registerChange("options");
    }

    public Set<WDTagOptGroupState> getGroupStates() {
        return new ChangesSupportSet<WDTagOptGroupState>(this, "groupStates", groupStates);
    }

    public void setGroupStates(Set<WDTagOptGroupState> groupStates) {
        this.groupStates = groupStates;
        this.registerChange("groupStates");
    }

    @Override
    public String toString() {
        return "WDTag[id=" + id + "]";
    }

    @Override
    public PublisherAccount getAccount() {
        return site == null ? null : site.getAccount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WDTag wdTag = (WDTag) o;

        if (id != null ? !id.equals(wdTag.id) : wdTag.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public Status getParentStatus() {
        return getSite().getInheritedStatus();
    }
}
