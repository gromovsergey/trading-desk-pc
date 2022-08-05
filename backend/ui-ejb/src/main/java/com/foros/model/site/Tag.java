package com.foros.model.site;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.SiteLinkXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.MarketplaceType;
import com.foros.model.account.PublisherAccount;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.security.OwnedStatusable;
import com.foros.util.FlagsUtil;
import com.foros.util.changes.ChangesSupportList;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.UrlConstraint;
import com.foros.validation.strategy.ValidationMode;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.security.DenyAll;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "TAGS")
@AllowedStatuses(values = { Status.ACTIVE, Status.DELETED })
@XmlRootElement(name = "tag")
@XmlType(propOrder = {
        "id",
        "site",
        "name"
})
@XmlAccessorType(XmlAccessType.NONE)
public class Tag extends StatusEntityBase implements OwnedStatusable<PublisherAccount>, DisplayStatusEntity, Serializable, IdNameEntity {

    public static final long TAG_LEVEL_EXCLUSION_FLAG = 0x1;

    /** 0 Adserving Mode, 1 Inventory Estimation Mode */
    public static final long INVENTORY_ESTIMATION_FLAG = 0x2;

    /** 0 - select sizes individually, 1 - enable all sizes in size type */
    public static final long ALL_SIZES_FLAG = 0x4;

    @GenericGenerator(name = "TagsGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator",
            parameters = {
                    @Parameter(name = "allocationSize", value = "20"),
                    @Parameter(name = "sequenceName", value = "TAGS_TAG_ID_SEQ") })
    @IdConstraint
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TagsGen")
    @Column(name = "TAG_ID", nullable = false)
    private Long id;

    @StringSizeConstraint(size = 100)
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "FLAGS")
    private Long flags = 0L;

    @UrlConstraint(schemas = {"//", "http://", "https://"})
    @StringSizeConstraint(size = 2000)
    @Column(name = "PASSBACK")
    private String passback;

    @ByteLengthConstraint(length = 4000)
    @Transient
    private String passbackHtml;

    @HasIdConstraint(excludedModes = {ValidationMode.BULK})
    @JoinColumn(name = "SITE_ID", referencedColumnName = "SITE_ID")
    @ManyToOne
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Site site;

    @OneToMany(mappedBy = "tags", cascade = CascadeType.PERSIST)
    @OrderBy("country DESC")
    private List<TagPricing> tagPricings = new LinkedList<TagPricing>();

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToMany(mappedBy = "tag")
    private Set<TagsCreativeCategoryExclusion> tagsExclusions = new LinkedHashSet<TagsCreativeCategoryExclusion>();

    @JoinTable(name = "TAGCONTENTCATEGORY",
        joinColumns = {@JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID")},
        inverseJoinColumns = {@JoinColumn(name = "CONTENT_CATEGORY_ID", referencedColumnName = "CONTENT_CATEGORY_ID")}
    )
    @ManyToMany
    private Set<ContentCategory> contentCategories = new LinkedHashSet<ContentCategory>();

    @Column(name = "MARKETPLACE")
    @Type(type = "com.foros.persistence.hibernate.type.GenericEnumType", parameters = {
            @Parameter(name = "enumClass", value = "com.foros.model.account.MarketplaceType"),
            @Parameter(name = "nullValue", value = "NOT_SET")
    })
    private MarketplaceType marketplaceType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tag")
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Fetch(FetchMode.SUBSELECT)
    private Set<TagOptionValue> options = new HashSet<TagOptionValue>();

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.MERGE}, mappedBy = "tag")
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Fetch(FetchMode.SUBSELECT)
    private Set<TagOptGroupState> groupStates = new LinkedHashSet<TagOptGroupState>();

    @Column(name = "ALLOW_EXPANDABLE")
    private boolean allowExpandable;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "tag.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "tag.displaystatus.deleted");

    @Column(name = "PASSBACK_TYPE")
    @Enumerated(EnumType.STRING)
    private PassbackType passbackType;

    @ChangesInspection(type = InspectionType.CASCADE)
    @OneToOne(mappedBy = "tag", fetch = FetchType.LAZY)
    private TagAuctionSettings auctionSettings;

    @ByteLengthConstraint(length = 4000)
    @Column(name = "PASSBACK_CODE")
    private String passbackCode;

    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @RequiredConstraint
    @OneToOne
    @JoinColumn(name = "SIZE_TYPE_ID")
    private SizeType sizeType;

    @JoinTable(name = "TAG_TAGSIZE",
            joinColumns = { @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID") },
            inverseJoinColumns = { @JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID") })
    @ManyToMany(cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("defaultName")
    private Set<CreativeSize> sizes = new LinkedHashSet<CreativeSize>();

    /**
     * Creates a new instance of Tag
     */
    public Tag() {
    }

    /**
     * Creates a new instance of Tag with the specified values.
     * @param id the tagId of the Tag
     */
    public Tag(Long id) {
        this.id = id;
    }

    /**
     * Creates a new instance of Tag with the specified values.
     * @param id the tagId of the Tag
     * @param name the name of the Tag
     */
    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
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

    /**
     * Gets the tagId of this Tag.
     * @return the tagId
     */
    @Override
    @XmlElement
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the tagId of this Tag to the specified value.
     *
     * @param id the new tagId
     */
    @Override
    @DenyAll
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * Gets the name of this Tag.
     * @return the name
     */
    @Override
    @XmlElement
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of this Tag to the specified value.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    /**
     * Gets the flags of this Tags.
     * @return the flags
     */
    public Long getFlags() {
        if (flags == null) {
            flags = 0L;
        }

        return flags;
    }

    /**
     * Sets the flags of this Tag to the specified value.
     *
     * @param flags the new flags
     */
    public void setFlags(Long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    /**
     * Gets the passback of this Tag.
     * @return the passback
     */
    public String getPassback() {
        return this.passback;
    }

    /**
     * Sets the passback of this Tag to the specified value.
     *
     * @param passback the new passback
     */
    public void setPassback(String passback) {
        this.passback = passback;
        this.registerChange("passback");
    }

    public String getPassbackHtml() {
        return passbackHtml;
    }

    public void setPassbackHtml(String passbackHtml) {
        this.passbackHtml = passbackHtml;
    }

    public Set<CreativeSize> getSizes() {
        return new ChangesSupportSet<CreativeSize>(this, "sizes", sizes);
    }

    public Set<CreativeSize> getEffectiveSizes() {
        return isAllSizesFlag() ? new HashSet<>(getAccount().getAccountType().findSizesBySizeTypeName(getSizeType().getDefaultName())) : getSizes();
    }

    public CreativeSize getOnlySizeOrNull() {
        Set<CreativeSize> effectiveSizes = getEffectiveSizes();
        return effectiveSizes.size() == 1 ? effectiveSizes.iterator().next() : null;
    }

    public CreativeSize getOnlySize() {
        CreativeSize res = getOnlySizeOrNull();
        if (res == null) {
            throw new IllegalArgumentException();
        }
        return res;
    }

    public void setSizes(Set<CreativeSize> sizes) {
        this.sizes = sizes;
        this.registerChange("sizes");
    }

    /**
     * Gets the siteId of this Tag.
     * @return the siteId
     */
    @XmlElement
    @XmlJavaTypeAdapter(SiteLinkXmlAdapter.class)
    public Site getSite() {
        return this.site;
    }

    /**
     * Sets the siteId of this Tag to the specified value.
     *
     * @param site the new siteId
     */
    public void setSite(Site site) {
        this.site = site;
        this.registerChange("site");
    }

    public List<TagPricing> getTagPricings() {
        return new ChangesSupportList<TagPricing>(this, "tagPricings", tagPricings);
    }

    public void setTagPricings(List<TagPricing> tagPricings) {
        this.tagPricings = tagPricings;
        this.registerChange("tagPricings");
    }

    public MarketplaceType getMarketplaceType() {
        return marketplaceType;
    }

    public void setMarketplaceType(MarketplaceType marketplaceType) {
        if (marketplaceType == null) {
            throw new NullPointerException();
        }
        this.marketplaceType = marketplaceType;
        this.registerChange("marketplaceType");
    }

    public boolean isAllowExpandable() {
        return allowExpandable;
    }

    public void setAllowExpandable(boolean allowExpandable) {
        this.allowExpandable = allowExpandable;
        this.registerChange("allowExpandable");

    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hash = 0;
        if(this.getId()==null) {
            return super.hashCode();
        }
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    /**
     * Determines whether another object is equal to this Tag.  The result is
     * <code>true</code> if and only if the argument is not null and is a Tag object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Tag)) {
            return false;
        }

        Tag other = (Tag)object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getName(), other.getName())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getFlags(), other.getFlags())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getPassback(), other.getPassback())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getSizes(), other.getSizes())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getSite(), other.getSite())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getTagPricings(), other.getTagPricings())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getMarketplaceType(), other.getMarketplaceType())) {
            return false;
        }

        return true;
    }

    public Set<TagOptGroupState> getGroupStates() {
        return new ChangesSupportSet<TagOptGroupState>(this, "groupStates", groupStates);
    }

    public void setGroupStates(Set<TagOptGroupState> groupStates) {
        this.groupStates = groupStates;
        this.registerChange("groupStates");
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.site.Tag[id=" + getId() + ",name=" + getName() + "]";
    }

    @Override
    public PublisherAccount getAccount() {
        return this.getSite() == null ? null : this.getSite().getAccount();
    }

    public Set<TagsCreativeCategoryExclusion> getTagsExclusions() {
        return new ChangesSupportSet<TagsCreativeCategoryExclusion>(this, "tagsExclusions", tagsExclusions);
    }

    public void setTagsExclusions(Set<TagsCreativeCategoryExclusion> tagsExclusions) {
        this.tagsExclusions = tagsExclusions;
        this.registerChange("tagsExclusions");
    }

    public boolean isTagLevelExclusionFlag() {
        return FlagsUtil.get(getFlags(), TAG_LEVEL_EXCLUSION_FLAG);
    }

    public void setTagLevelExclusionFlag (boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), TAG_LEVEL_EXCLUSION_FLAG, flag));
    }

    public boolean isInventoryEstimationFlag() {
        return FlagsUtil.get(getFlags(), INVENTORY_ESTIMATION_FLAG);
    }

    public void setInventoryEstimationFlag (boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), INVENTORY_ESTIMATION_FLAG, flag));
    }

    public boolean isAllSizesFlag() {
        return FlagsUtil.get(getFlags(), ALL_SIZES_FLAG);
    }

    public void setAllSizesFlag(boolean flag) {
        setFlags(FlagsUtil.set(getFlags(), ALL_SIZES_FLAG, flag));
    }

    @Override
    public Status getParentStatus() {
        return getSite().getInheritedStatus();
    }

    public Set<ContentCategory> getContentCategories() {
        return new ChangesSupportSet<ContentCategory>(this, "contentCategories", contentCategories);
    }

    public void setContentCategories(Set<ContentCategory> contentCategories) {
        this.contentCategories = contentCategories;
        this.registerChange("contentCategories");
    }

    public Set<TagOptionValue> getOptions() {
        return new ChangesSupportSet<TagOptionValue>(this, "options", options);
    }

    public void setOptions(Set<TagOptionValue> options) {
        this.options = options;
        this.registerChange("options");
    }

    public PassbackType getPassbackType() {
        return passbackType;
    }

    public void setPassbackType(PassbackType passbackType) {
        this.passbackType = passbackType;
        this.registerChange("passbackType");
    }

    public TagAuctionSettings getAuctionSettings() {
        return auctionSettings;
    }

    public void setAuctionSettings(TagAuctionSettings auctionSettings) {
        this.auctionSettings = auctionSettings;
        this.registerChange("auctionSettings");
    }

    public String getPassbackCode() {
        return passbackCode;
    }

    public void setPassbackCode(String passbackCode) {
        this.passbackCode = passbackCode;
        this.registerChange("passbackCode");
    }

    public SizeType getSizeType() {
        return sizeType;
    }

    public void setSizeType(SizeType sizeType) {
        this.sizeType = sizeType;
        this.registerChange("sizeType");
    }
}
