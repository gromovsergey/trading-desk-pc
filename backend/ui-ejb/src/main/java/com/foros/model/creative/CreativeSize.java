package com.foros.model.creative;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.custom.CollectionStringValueChange;
import com.foros.jaxb.adapters.SizeTypeXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.security.AccountType;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroup;
import com.foros.model.template.OptionGroupType;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.DenyAll;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "CREATIVESIZE")
@NamedQueries({
    @NamedQuery(name = "CreativeSize.findAll", query = "SELECT c FROM CreativeSize c"),
    @NamedQuery(name = "CreativeSize.findAllNotDeleted", query = "SELECT c FROM CreativeSize c WHERE NOT c.status = 'D'"),
    @NamedQuery(name = "CreativeSize.findById", query = "SELECT c FROM CreativeSize c WHERE c.id = :id"),
    @NamedQuery(name = "CreativeSize.findByStatus", query = "SELECT c FROM CreativeSize c WHERE c.status = :status"),
    @NamedQuery(name = "CreativeSize.findByProtocolName", query = "SELECT c FROM CreativeSize c WHERE c.protocolName = :protocolName"),
    @NamedQuery(name = "CreativeSize.findByWidth", query = "SELECT c FROM CreativeSize c WHERE c.width = :width"),
    @NamedQuery(name = "CreativeSize.findByHeight", query = "SELECT c FROM CreativeSize c WHERE c.height = :height"),
    @NamedQuery(name = "CreativeSize.findByAccType",
                query = "SELECT c FROM CreativeSize c JOIN c.accountTypes a WHERE a = :accType"),
    @NamedQuery(name = "CreativeSize.findAllNotDeletedExceptText",
                query = "SELECT c FROM CreativeSize c WHERE NOT c.status = 'D' and c.defaultName <> :name")
})
@AllowedStatuses(values = { Status.ACTIVE, Status.DELETED })
@XmlRootElement(name = "creativeSize")
@XmlType(propOrder = {
        "id",
        "protocolName",
        "defaultName",
        "sizeType",
        "width",
        "height",
        "maxWidth",
        "maxHeight",
        "optionGroups",
        "expansions"
})
@XmlAccessorType(XmlAccessType.NONE)
public class CreativeSize extends StatusEntityBase implements LocalizableNameEntity, DisplayStatusEntity {
    public static final String TEXT_SIZE = "Text";

    public static final Comparator<CreativeSize> BY_PROTOCOL_COMPARATOR = new Comparator<CreativeSize>() {
        @Override
        public int compare(CreativeSize o1, CreativeSize o2) {
            return o1.getProtocolName().compareTo(o2.getProtocolName());
        }
    };

    @SequenceGenerator(name = "CreativeSizeGen", sequenceName = "CREATIVESIZE_SIZE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CreativeSizeGen")
    @Column(name = "SIZE_ID", nullable = false)
    private Long id;

    @Column(name = "PROTOCOL_NAME", nullable = false)
    @PatternConstraint(regexp = "[\\w]*", message="errors.field.onlyAlphabetAndUnderscore")
    @RequiredConstraint
    private String protocolName;

    @Column(name = "NAME", nullable = false)
    @NameConstraint
    @RequiredConstraint
    private String defaultName;

    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    @RequiredConstraint
    @HasIdConstraint
    @ManyToOne
    @JoinColumn(name = "SIZE_TYPE_ID")
    private SizeType sizeType;

    @Column(name = "WIDTH")
    @RangeConstraint(min = "1", max = "9999")
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Long width;

    @Column(name = "HEIGHT")
    @RangeConstraint(min = "1", max = "9999")
    private Long height;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "creativeSize", cascade = {CascadeType.ALL})
    @OrderBy("sortOrder")
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<OptionGroup> optionGroups = new LinkedHashSet<OptionGroup>();

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "size", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.SETNULL)
    private Set<Creative> creatives = new LinkedHashSet<Creative>();

    @ManyToMany(mappedBy = "creativeSizes")
    @CopyPolicy(strategy = CopyStrategy.SETNULL)
    private Set<AccountType> accountTypes = new LinkedHashSet<AccountType>();

    @Column(name = "MAX_HEIGHT")
    @RangeConstraint(min = "1", max = "9999")
    private Long maxHeight;

    @Column(name = "MAX_WIDTH")
    @RangeConstraint(min = "1", max = "9999")
    private Long maxWidth;

    @ElementCollection(targetClass = CreativeSizeExpansion.class, fetch = FetchType.EAGER)
    @JoinTable(name = "CREATIVESIZEEXPANSION", joinColumns = @JoinColumn(name = "SIZE_ID", nullable = false))
    @Enumerated(EnumType.STRING)
    @Column(name = "EXPANSION", nullable = true, insertable = true, updatable = true)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Audit(nodeFactory = CollectionStringValueChange.Factory.class)
    private Set<CreativeSizeExpansion> expansions = new LinkedHashSet<CreativeSizeExpansion>();

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "creativeSize.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "creativeSize.displaystatus.deleted");

    public CreativeSize() {
    }

    public CreativeSize(Long id) {
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

    @Override
    public LocalizableName getName() {
        return LocalizableNameProvider.CREATIVE_SIZE.provide(defaultName, id);
    }

    @XmlElement
    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        this.registerChange("defaultName");
    }

    @XmlElement
    @XmlJavaTypeAdapter(SizeTypeXmlAdapter.class)
    public SizeType getSizeType() {
        return sizeType;
    }

    public void setSizeType(SizeType sizeType) {
        this.sizeType = sizeType;
        this.registerChange("sizeType");
    }

    @XmlElement
    public String getProtocolName() {
        return this.protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
        this.registerChange("protocolName");
    }

    @XmlElement
    public Long getWidth() {
        return this.width;
    }

    public void setWidth(Long width) {
        this.width = width;
        this.registerChange("width");
    }

    @XmlElement
    public Long getHeight() {
        return this.height;
    }

    public void setHeight(Long height) {
        this.height = height;
        this.registerChange("height");
    }

    @Transient
    public Collection<Option> getAllOptions() {
        ArrayList<Option> options = new ArrayList<Option>();
        for (OptionGroup optionGroup: getOptionGroups()) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    @XmlElement(name = "optionGroup")
    @XmlElementWrapper(name = "optionGroups")
    public Set<OptionGroup> getOptionGroups() {
        return new ChangesSupportSet<OptionGroup>(this, "optionGroups", optionGroups);
    }


    public void setOptionGroups(Set<OptionGroup> optionGroups) {
        this.optionGroups = optionGroups;
        this.registerChange("optionGroups");
    }

    public Set<Creative> getCreatives() {
        return new ChangesSupportSet<Creative>(this, "creatives", creatives);
    }

    public void setCreatives(Set<Creative> creatives) {
        this.creatives = creatives;
        this.registerChange("creatives");
    }

    public Set<AccountType> getAccountTypes() {
        return new ChangesSupportSet<AccountType>(this, "accountTypes", accountTypes);
    }

    public void setAccountTypes(Set<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
        this.registerChange("accountTypes");
    }

    @XmlElement
    public Long getMaxHeight() {
        return maxHeight;
    }

    @XmlElement
    public Long getMaxWidth() {
        return maxWidth;
    }

    public void setMaxHeight(Long maxHeight) {
        this.maxHeight = maxHeight;
        this.registerChange("maxHeight");
    }

    @XmlElement(name = "expansion")
    @XmlElementWrapper(name = "expansions")
    public Set<CreativeSizeExpansion> getExpansions() {
        return new ChangesSupportSet<CreativeSizeExpansion>(this, "expansions", expansions);
    }

    public void setExpansions(Set<CreativeSizeExpansion> creativeSizeExpansions) {
        this.expansions = creativeSizeExpansions;
        this.registerChange("expansions");
    }

    public void setMaxWidth(Long maxWidth) {
        this.maxWidth = maxWidth;
        this.registerChange("maxWidth");
    }

    public boolean isText() {
        return isTextName(getName());
    }

    @Override
    public String toString() {
        return "com.foros.model.creative.CreativeSize[id=" + getId() + "]";
    }

    public static boolean isTextName(LocalizableName name) {
        return name != null && TEXT_SIZE.equals(name.getDefaultName());
    }

    @Override
    public Status getParentStatus() {
        return Status.ACTIVE;
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

    public boolean isExpandable() {
        return !expansions.isEmpty();
    }

    public Set<OptionGroup> getOptionGroupsByGroupType(OptionGroupType groupType) {
        Set<OptionGroup> groups = new LinkedHashSet<OptionGroup>();
        for (OptionGroup optionGroup : getOptionGroups()) {
            if (groupType == optionGroup.getType()) {
                groups.add(optionGroup);
            }
        }
        return new ChangesSupportSet<OptionGroup>(this, "optionGroups", groups);
    }

    public List<Option> getOptionsByGroupType(OptionGroupType groupType) {
        List<Option> options = new ArrayList<>();
        for (OptionGroup optionGroup : getOptionGroupsByGroupType(groupType)) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    public Set<OptionGroup> getPublisherOptionGroups() {
        return getOptionGroupsByGroupType(OptionGroupType.Publisher);
    }

    public Set<OptionGroup> getAdvertiserOptionGroups() {
        return getOptionGroupsByGroupType(OptionGroupType.Advertiser);
    }

    public Set<OptionGroup> getHiddenOptionGroups() {
        return getOptionGroupsByGroupType(OptionGroupType.Hidden);
    }

    public List<Option> getPublisherOptions() {
        return getOptionsByGroupType(OptionGroupType.Publisher);
    }

    public Collection<Option> getAdvertiserOptions() {
        return getOptionsByGroupType(OptionGroupType.Advertiser);
    }

    public Collection<Option> getHiddenOptions() {
        return getOptionsByGroupType(OptionGroupType.Hidden);
    }

    public Option getOptionByToken(OptionGroupType groupType, String token) {
        for (OptionGroup optionGroup : optionGroups) {
            if (groupType != optionGroup.getType()) {
                continue;
            }
            for (Option option : optionGroup.getOptions()) {
                if (option.getToken().equals(token)) {
                    return option;
                }
            }
        }
        return null;
    }

    public boolean hasOption(Option option) {
        CreativeSize size = option.getOptionGroup().getCreativeSize();
        return size == null ? false : size.getId().equals(getId());
    }
}
