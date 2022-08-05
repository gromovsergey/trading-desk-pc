package com.foros.model.site;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.ForceFieldChangeEntityChange;
import com.foros.jaxb.adapters.AccountLinkXmlAdapter;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.FrequencyCapEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.OwnedApprovable;
import com.foros.model.Status;
import com.foros.model.account.PublisherAccount;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.UrlConstraint;
import com.foros.validation.strategy.ValidationMode;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "SITE")
@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
@AllowedQAStatuses(values = { ApproveStatus.APPROVED, ApproveStatus.DECLINED, ApproveStatus.HOLD })
@NamedQueries({
    @NamedQuery(name = "Site.countByName", query = "SELECT count(s) FROM Site s where s.name = :name and s.account.id = :accountId"),
    @NamedQuery(name = "Site.countByIdName", query = "SELECT count(s) FROM Site s where s.name = :name and s.id <> :id and s.account.id = :accountId")
})
@Audit(nodeFactory = ForceFieldChangeEntityChange.Factory.class)
@XmlRootElement(name = "site")
@XmlType(propOrder = {
        "id",
        "name",
        "account"
})
public class Site extends ApprovableEntity implements OwnedApprovable<PublisherAccount>, Serializable, IdNameEntity, FrequencyCapEntity {

    @GenericGenerator(name = "SiteGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "SITE_SITE_ID_SEQ") })
    @IdConstraint
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SiteGen")
    @Column(name = "SITE_ID", nullable = false)
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @RangeConstraint(min = "0", max = "2147483647")
    @Column(name = "NO_ADS_TIMEOUT", nullable = false)
    private Long noAdsTimeout;

    @RequiredConstraint
    @UrlConstraint
    @Column(name = "SITE_URL")
    private String siteUrl;

    @Column(name = "FLAGS")
    private Long flags;

    @StringSizeConstraint(size = 2000)
    @Column(name = "NOTES")
    private String notes;

    @NotNullConstraint(excludedModes = { ValidationMode.BULK })
    @HasIdConstraint(excludedModes = { ValidationMode.BULK })
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private PublisherAccount account;

    @JoinColumn(name = "FREQ_CAP_ID", referencedColumnName = "FREQ_CAP_ID")
    @ManyToOne(cascade = CascadeType.ALL)
    private FrequencyCap frequencyCap;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "site")
    private Set<Tag> tags = new LinkedHashSet<Tag>();

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "site")
    private Set<WDTag> wdTags = new LinkedHashSet<WDTag>();

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private Set<SiteCreativeCategoryExclusion> categoryExclusions = new LinkedHashSet<SiteCreativeCategoryExclusion>();

    @OneToMany
    @JoinColumn(name="SITE_ID", referencedColumnName="SITE_ID")
    @ChangesInspection(type = InspectionType.CASCADE)
    private Set<SiteCreativeApproval> creativeApprovals = new LinkedHashSet<SiteCreativeApproval>();

    @NotNullConstraint(excludedModes = {ValidationMode.BULK})
    @HasIdConstraint
    @JoinColumn(name = "SITE_CATEGORY_ID", referencedColumnName = "SITE_CATEGORY_ID")
    @ManyToOne
    private SiteCategory siteCategory;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "site.displaystatus.live");
    public static final DisplayStatus NO_ACTIVE_TAGS = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "site.displaystatus.no_active_tags");
    public static final DisplayStatus DECLINED = new DisplayStatus(3L, DisplayStatus.Major.NOT_LIVE, "site.displaystatus.declined");
    public static final DisplayStatus PENDING_FOROS = new DisplayStatus(4L, DisplayStatus.Major.NOT_LIVE, "site.displaystatus.pending_foros");
    public static final DisplayStatus DELETED = new DisplayStatus(6L, DisplayStatus.Major.DELETED, "site.displaystatus.deleted");

    public static Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        NO_ACTIVE_TAGS,
        DECLINED,
        PENDING_FOROS,
        DELETED
    );

    public static DisplayStatus getDisplayStatus(Long id) {
        return displayStatusMap.get(id);
    }

    @Override
    public DisplayStatus getDisplayStatus() {
        return Site.getDisplayStatus(displayStatusId);
    }

    static public Collection<DisplayStatus> getAvailableDisplayStatuses() {
        return displayStatusMap.values();
    }

    static public DisplayStatus getDisplayStatusPA_FOROS() {
        return PENDING_FOROS;
    }

    public Site() {
    }

    public Site(Long id) {
        this.id = id;
    }

    public Site(Long id, String name) {
        this(id);
        this.name = name;
    }

    @Override
    @XmlElement
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @Override
    @XmlElement
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @XmlTransient
    public Long getNoAdsTimeout() {
        return this.noAdsTimeout;
    }

    public void setNoAdsTimeout(Long noAdsTimeout) {
        this.noAdsTimeout = noAdsTimeout;
        this.registerChange("noAdsTimeout");
    }

    @XmlTransient
    public String getSiteUrl() {
        return this.siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
        this.registerChange("siteUrl");
    }

    @XmlTransient
    public Long getFlags() {
        return this.flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    @XmlTransient
    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.registerChange("notes");
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(AccountLinkXmlAdapter.class)
    public PublisherAccount getAccount() {
        return this.account;
    }

    public void setAccount(PublisherAccount account) {
        this.account = account;
        this.registerChange("account");
    }

    @Override
    @XmlTransient
    public FrequencyCap getFrequencyCap() {
        return this.frequencyCap;
    }

    @Override
    public void setFrequencyCap(FrequencyCap frequencyCap) {
        this.frequencyCap = frequencyCap;
        this.registerChange("frequencyCap");
    }

    @XmlTransient
    public Set<Tag> getTags() {
        return new ChangesSupportSet<Tag>(this, "tags", tags);
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
        this.registerChange("tags");
    }

    @XmlTransient
    public Set<WDTag> getWdTags() {
        return new ChangesSupportSet<WDTag>(this, "wdTags", wdTags);
    }

    public void setWdTags(Set<WDTag> tags) {
        this.wdTags = tags;
        this.registerChange("wdTags");
    }

    @XmlTransient
    public Set<SiteCreativeCategoryExclusion> getCategoryExclusions() {
        return new ChangesSupportSet<SiteCreativeCategoryExclusion>(this, "categoryExclusions", categoryExclusions);
    }

    public void setCategoryExclusions(Set<SiteCreativeCategoryExclusion> categoryExclusions) {
        this.categoryExclusions = categoryExclusions;
        this.registerChange("categoryExclusions");
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
        if (!(object instanceof Site)) {
            return false;
        }
        Site other = (Site)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @XmlTransient
    public boolean isApproved() {
        return getQaStatus() == ApproveStatus.APPROVED;
    }

    @Override
    public String toString() {
        return "Site[id=" + getId() + ",name=" + getName() + "]";
    }

    @Override
    @XmlTransient
    public Status getParentStatus() {
        return getAccount().getInheritedStatus();
    }

    @XmlTransient
    public SiteCategory getSiteCategory() {
        return siteCategory;
    }

    public void setSiteCategory(SiteCategory siteCategory) {
        this.siteCategory = siteCategory;
        this.registerChange("siteCategory");
    }

}
