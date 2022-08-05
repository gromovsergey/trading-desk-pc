package com.foros.model.creative;

import com.foros.annotations.AllowedQAStatuses;
import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.AdvertiserAgencyAccountXmlAdapter;
import com.foros.jaxb.adapters.CreativeCategoryXmlAdapter;
import com.foros.jaxb.adapters.CreativeSizeXmlAdapter;
import com.foros.jaxb.adapters.CreativeTemplateXmlAdapter;
import com.foros.jaxb.adapters.SizeTypeXmlAdapter;
import com.foros.model.ApprovableEntity;
import com.foros.model.ApproveStatus;
import com.foros.model.DisplayStatus;
import com.foros.model.ExtensionProperty;
import com.foros.model.Flags;
import com.foros.model.IdNameEntity;
import com.foros.model.OwnedApprovable;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.TnsBrand;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.HtmlSymbolsOnlyConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CREATIVE")
@NamedQueries(
  {
    @NamedQuery(name = "Creative.findById", query = "SELECT c FROM Creative c WHERE c.id = :id"),

    @NamedQuery(name = "Creative.entityTO.findByAdvertiserId", query = "SELECT DISTINCT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND c.account.id = :accountId  and (:isInternal = true or c.status <> 'D') and (:isOnlyTextAds = false or cc.creativeGroup.ccgType = 'T') ORDER BY c.name"),
    @NamedQuery(name = "Creative.entityTO.findByAdvertiserIdAndTargetType", query = "SELECT DISTINCT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND c.account.id = :accountId  and (:isInternal = true or c.status <> 'D') and (:isOnlyTextAds = false or cc.creativeGroup.ccgType = 'T') and (:targetType = cc.creativeGroup.tgtType) ORDER BY c.name"),
    @NamedQuery(name = "Creative.findByCampaignId", query = "SELECT DISTINCT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND cc.creativeGroup.campaign.id = :campaignId and (:isInternal = true or c.status <> 'D') and (:isOnlyTextAds = false or cc.creativeGroup.ccgType = 'T') ORDER BY c.name"),
    @NamedQuery(name = "Creative.findByCampaignIdAndTargetType", query = "SELECT DISTINCT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND cc.creativeGroup.campaign.id = :campaignId and (:isInternal = true or c.status <> 'D') and (:isOnlyTextAds = false or cc.creativeGroup.ccgType = 'T') and (:targetType = cc.creativeGroup.tgtType) ORDER BY c.name"),
    @NamedQuery(name = "Creative.findByCreativeGroupId", query = "SELECT DISTINCT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND cc.creativeGroup.id = :creativeGroupId and (:isInternal = true or c.status <> 'D') ORDER BY c.name"),
    @NamedQuery(name = "Creative.findByCategory", query = "SELECT c FROM Creative c WHERE :category MEMBER OF c.categories"),
    @NamedQuery(name = "Creative.findCreativeGroupsByGroupId", query = "SELECT cc FROM Creative c, CampaignCreative cc WHERE cc.creative = c AND cc.creativeGroup.id = :creativeGroupId")
  })
@AllowedStatuses(values = { Status.ACTIVE, Status.INACTIVE, Status.DELETED, Status.PENDING })
@AllowedQAStatuses(values = { ApproveStatus.APPROVED, ApproveStatus.DECLINED, ApproveStatus.HOLD })
@XmlRootElement(name = "creative")
@XmlType(propOrder = {
        "id",
        "account",
        "name",
        "size",
        "template",
        "enableAllAvailableSizes",
        "sizeTypes",
        "tagSizes",
        "options",
        "categories",
        "width",
        "height",
        "displayStatusId"
})
@XmlAccessorType(XmlAccessType.NONE)
public class Creative extends ApprovableEntity implements OwnedApprovable<AdvertiserAccount>, IdNameEntity, Serializable {
    private static final ExtensionProperty<Long> WIDTH = new ExtensionProperty<>(Long.class);
    private static final ExtensionProperty<Long> HEIGHT = new ExtensionProperty<>(Long.class);

    public static final int ENABLE_ALL_AVAILABLE_SIZES_MASK = 0x1;

    @Id
    @GeneratedValue(generator = "CreativeGen")
    @GenericGenerator(name = "CreativeGen", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator", parameters = {
                    @Parameter(name = "sequenceName", value = "CREATIVE_CREATIVE_ID_SEQ"),
                    @Parameter(name = "allocationSize", value = "20")
            }
    )
    @IdConstraint
    @Column(name = "CREATIVE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @RequiredConstraint
    @StringSizeConstraint(size = 150)
    @HtmlSymbolsOnlyConstraint
    @Column(name = "NAME")
    private String name;

    @Column(name = "FLAGS", nullable = false)
    @Type(type = "com.foros.persistence.hibernate.type.FlagsType")
    private Flags flags = Flags.ZERO;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creative")
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Set<CreativeOptionValue> options = new LinkedHashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creative")
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Set<CreativeOptGroupState> groupStates = new LinkedHashSet<>();

    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private AdvertiserAccount account;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private CreativeSize size;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private CreativeTemplate template;

    @JoinTable(name = "CREATIVECATEGORY_CREATIVE",
      joinColumns = {@JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID")},
      inverseJoinColumns = {@JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID")})
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<CreativeCategory> categories = new LinkedHashSet<>();

    @Column(name = "EXPANDABLE")
    private boolean expandable = false;

    @Column(name = "EXPANSION")
    @Enumerated(EnumType.STRING)
    private CreativeSizeExpansion expansion;

    @JoinTable(name = "CREATIVE_TagSizeType",
            joinColumns = {@JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SIZE_TYPE_ID", referencedColumnName = "SIZE_TYPE_ID")})
    @ManyToMany
    @CopyPolicy(strategy = CopyStrategy.CLONE, cloner = ShallowCollectionCloner.class, type = HashSet.class)
    private Set<SizeType> sizeTypes = new HashSet<>();

    @JoinTable(name = "CREATIVE_TagSize",
            joinColumns = {@JoinColumn(name = "CREATIVE_ID", referencedColumnName = "CREATIVE_ID")},
            inverseJoinColumns = {@JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID")})
    @ManyToMany
    @CopyPolicy(strategy = CopyStrategy.CLONE, cloner = ShallowCollectionCloner.class, type = HashSet.class)
    private Set<CreativeSize> tagSizes = new HashSet<>();

    @JoinColumn(name = "TNS_BRAND_ID", referencedColumnName = "TNS_BRAND_ID")
    @ManyToOne
    private TnsBrand tnsBrand;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "creative.displaystatus.live");
    public static final DisplayStatus DECLINED = new DisplayStatus(2L, DisplayStatus.Major.NOT_LIVE, "creative.displaystatus.declined");
    public static final DisplayStatus PENDING_FOROS = new DisplayStatus(3L, DisplayStatus.Major.NOT_LIVE, "creative.displaystatus.pending_foros");
    public static final DisplayStatus PENDING_USER = new DisplayStatus(4L, DisplayStatus.Major.NOT_LIVE, "creative.displaystatus.pending_user");
    public static final DisplayStatus INACTIVE = new DisplayStatus(5L, DisplayStatus.Major.INACTIVE, "creative.displaystatus.inactive");
    public static final DisplayStatus DELETED = new DisplayStatus(6L, DisplayStatus.Major.DELETED, "creative.displaystatus.deleted");

    public static final Map<Long, DisplayStatus> displayStatusMap = getDisplayStatusMap(
        LIVE,
        DECLINED,
        PENDING_FOROS,
        PENDING_USER,
        INACTIVE,
        DELETED
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

    public static DisplayStatus getDisplayStatusPA_User() {
        return PENDING_USER;
    }

    public static DisplayStatus getDisplayStatusPA_Foros() {
        return PENDING_FOROS;
    }

    public Creative() {
    }

    public Creative(Long id) {
        this.id = id;
    }

    @XmlElement
    @Override
    public Long getId() {
        return this.id;
    }

    @DenyAll
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @XmlElement(name = "option")
    @XmlElementWrapper(name = "options")
    public Set<CreativeOptionValue> getOptions() {
        return ChangesSupportSet.wrap(this, "options", options);
    }

    public void setOptions(Set<CreativeOptionValue> options) {
        this.options = ChangesSupportSet.unwrap(options);
        this.registerChange("options");
    }

    public Set<CreativeOptGroupState> getGroupStates() {
        return ChangesSupportSet.wrap(this, "groupStates", groupStates);
    }

    public void setGroupStates(Set<CreativeOptGroupState> groupStates) {
        this.groupStates = ChangesSupportSet.unwrap(groupStates);
        this.registerChange("groupStates");
    }

    @XmlElement
    @XmlJavaTypeAdapter(AdvertiserAgencyAccountXmlAdapter.class)
    @Override
    public AdvertiserAccount getAccount() {
        return this.account;
    }

    public void setAccount(AdvertiserAccount advertiserAccount) {
        this.account = advertiserAccount;
        this.registerChange("account");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CreativeSizeXmlAdapter.class)
    public CreativeSize getSize() {
        return this.size;
    }

    public void setSize(CreativeSize creativeSize) {
        this.size = creativeSize;
        this.registerChange("size");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CreativeTemplateXmlAdapter.class)
    public CreativeTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(CreativeTemplate creativeTemplate) {
        this.template = creativeTemplate;
        this.registerChange("template");
    }

    @XmlElement(name = "category")
    @XmlElementWrapper(name = "categories")
    @XmlJavaTypeAdapter(CreativeCategoryXmlAdapter.class)
    public Set<CreativeCategory> getCategories() {
        return ChangesSupportSet.wrap(this, "categories", categories);
    }

    public void setCategories(Set<CreativeCategory> categories) {
        this.categories = ChangesSupportSet.unwrap(categories);
        this.registerChange("categories");
    }

    public boolean isApproved() {
        return getQaStatus() == ApproveStatus.APPROVED;
    }

    public boolean isTextCreative() {
        return getSize() != null && getTemplate() != null && getSize().isText() && getTemplate().isText();
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
        this.registerChange("expandable");
    }

    public CreativeSizeExpansion getExpansion() {
        return expansion;
    }

    public void setExpansion(CreativeSizeExpansion expansion) {
        this.expansion = expansion;
        this.registerChange("expansion");
    }

    @XmlElement(name = "sizeType")
    @XmlElementWrapper(name = "sizeTypes")
    @XmlJavaTypeAdapter(SizeTypeXmlAdapter.class)
    public Set<SizeType> getSizeTypes() {
        return ChangesSupportSet.wrap(this, "sizeTypes", sizeTypes);
    }

    public void setSizeTypes(Set<SizeType> sizeTypes) {
        this.sizeTypes = ChangesSupportSet.unwrap(sizeTypes);
        this.registerChange("sizeTypes");
    }

    @XmlElement(name = "tagSize")
    @XmlElementWrapper(name = "tagSizes")
    @XmlJavaTypeAdapter(CreativeSizeXmlAdapter.class)
    public Set<CreativeSize> getTagSizes() {
        return ChangesSupportSet.wrap(this, "tagSizes", tagSizes);
    }

    public void setTagSizes(Set<CreativeSize> tagSizes) {
        this.tagSizes = ChangesSupportSet.unwrap(tagSizes);
        this.registerChange("tagSizes");
    }

    @XmlElement(name = "enableAllAvailableSizes")
    public Boolean isEnableAllAvailableSizes() {
        return flags.get(ENABLE_ALL_AVAILABLE_SIZES_MASK);
    }

    public void setEnableAllAvailableSizes(Boolean value) {
        flags = flags.set(ENABLE_ALL_AVAILABLE_SIZES_MASK, value);
        this.registerChange("flags");
        this.registerChange("enableAllAvailableSizes");
    }

    @Override
    @XmlElement
    public Long getDisplayStatusId() {
        return super.getDisplayStatusId();
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
        if (!(object instanceof Creative)) {
            return false;
        }
        Creative other = (Creative)object;
        return this.getId() == other.getId() || this.getId() != null && this.getId().equals(other.getId());
    }

    @Override
    public String toString() {
        return "com.foros.model.creative.Creative[id=" + getId() + "]";
    }

    @Override
    public Status getParentStatus() {
        return account.getInheritedStatus();
    }

    public CreativeOptionValue findOptionValue(Option option) {
        for (CreativeOptionValue value : options) {
            Option srcOption = value.getOption();
            if (srcOption != null && srcOption.getId() != null && srcOption.getId().equals(option.getId())) {
                return value;
            }
        }
        return null;
    }

    @XmlElement
    public Long getWidth() {
        return getProperty(WIDTH);
    }

    public void setWidth(Long width) {
        setProperty(WIDTH, width);
    }

    @XmlElement
    public Long getHeight() {
        return getProperty(HEIGHT);
    }

    public void setHeight(Long height) {
        setProperty(HEIGHT, height);
    }

    public TnsBrand getTnsBrand() {
        return tnsBrand;
    }

    public void setTnsBrand(TnsBrand tnsBrand) {
        this.tnsBrand = tnsBrand;
        this.registerChange("tnsBrand");
    }
}
