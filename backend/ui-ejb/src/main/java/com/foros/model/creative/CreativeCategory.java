package com.foros.model.creative;

import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.CreativeCategoryAuditSerializer;
import com.foros.changes.inspection.changeNode.CreativeCategoryEntityChange;
import com.foros.jaxb.adapters.QaStatusAdapter;
import com.foros.jaxb.adapters.RTBCategoryToKeyNameXmlAdapter;
import com.foros.model.ApproveStatus;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "CREATIVECATEGORY")
@NamedQueries({
    @NamedQuery(name = "CreativeCategory.findById", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.id = :id"),
    @NamedQuery(name = "CreativeCategory.findByType", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.type = :type order by c.defaultName"),
    @NamedQuery(name = "CreativeCategory.findByTypeStatus", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.type = :type and c.qaStatus = :qaStatus order by c.defaultName"),
    @NamedQuery(name = "CreativeCategory.findByTypeName", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.type = :type and c.defaultName = :name"),
    @NamedQuery(name = "CreativeCategory.findByTypeNameStatus", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.type = :type and c.defaultName = :name and c.qaStatus = :qaStatus"),
    @NamedQuery(name = "CreativeCategory.findByStatus", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories WHERE c.qaStatus = :qaStatus ORDER BY c.type, c.defaultName "),
    @NamedQuery(name = "CreativeCategory.findAll", query = "SELECT DISTINCT c FROM CreativeCategory c LEFT JOIN FETCH c.rtbCategories")
})
@Audit(serializer = CreativeCategoryAuditSerializer.class, nodeFactory = CreativeCategoryEntityChange.Factory.class)
@XmlRootElement(name = "creativeCategory")
@XmlType(propOrder = {
        "qaStatusObj",
        "id",
        "type",
        "defaultName",
        "rtbCategories"
})
@XmlAccessorType(XmlAccessType.NONE)
public class CreativeCategory extends VersionEntityBase implements LocalizableNameEntity {
    @SequenceGenerator(name = "CreativeCategoryGen", sequenceName = "CREATIVECATEGORY_CREATIVE_CATEGORY_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CreativeCategoryGen")
    @Column(name = "CREATIVE_CATEGORY_ID", nullable = false)
    private Long id;

    @RequiredConstraint
    @StringSizeConstraint(size = 100)
    @PatternConstraint(regexp = "^([\\p{L}\\p{Nd}\\.\\-& ]+)$", message="errors.field.categoryNameSymbols")
    @Column(name = "NAME", nullable = false)
    private String defaultName;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CCT_ID")
    private CreativeCategoryType type;

    @Column(name = "QA_STATUS", nullable = false)
    private char qaStatus = 'A';

    @OneToMany(mappedBy = "creativeCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RTBCategory> rtbCategories = new LinkedHashSet<>();

    /** Creates a new instance of CreativeCategory */
    public CreativeCategory() {
    }

    /**
     * Creates a new instance of CreativeCategory with the specified values.
     * @param id the creativeCategoryId of the CreativeCategory
     */
    public CreativeCategory(Long id) {
        this.id = id;
    }

    /**
     * Creates a new instance of CreativeCategory with the specified values.
     * @param id the creativeCategoryId of the CreativeCategory
     * @param defaultName the name of the CreativeCategory
     */
    public CreativeCategory(Long id, String defaultName) {
        this.id = id;
        this.defaultName = defaultName;
    }

    /**
     * Gets the creativeCategoryId of this CreativeCategory.
     * @return the creativeCategoryId
     */
    @XmlElement
    @Override
    public Long getId() {
        return this.id;
    }

    /**
     * Sets the creativeCategoryId of this CreativeCategory to the specified value.
     *
     * @param id the new creativeCategoryId
     */
    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @Override
    public LocalizableName getName() {
        return LocalizableNameProvider.CREATIVE_CATEGORY.provide(getDefaultName(), getId());
    }

    /**
     * Gets the name of this CreativeCategory.
     * @return the name
     */
    @XmlElement(name = "name")
    public String getDefaultName() {
        return this.defaultName;
    }

    /**
     * Sets the name of this CreativeCategory to the specified value.
     *
     * @param name the new name
     */
    public void setDefaultName(String name) {
        this.defaultName = name;
        this.registerChange("defaultName");
    }

    @XmlElement
    public CreativeCategoryType getType() {
        return type;
    }

    public void setType(CreativeCategoryType type) {
        this.type = type;
        this.registerChange("type");
    }

    @XmlJavaTypeAdapter(QaStatusAdapter.class)
    @XmlElement(name = "qaStatus")
    public ApproveStatus getQaStatusObj() {
        return ApproveStatus.valueOf(qaStatus);
    }

    public char getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(char qaStatus) {
        this.qaStatus = qaStatus;
        this.registerChange("qaStatus");
    }



    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }

        int hash = 7;
        hash = 31 * hash + (getDefaultName() == null ? 0 : getDefaultName().hashCode());
        hash = 31 * hash + (getType() == null ? 0 : getType().hashCode());
        return hash;
    }

    /**
     * Determines whether another object is equal to this CreativeCategory.  The result is
     * <code>true</code> if and only if the argument is not null and is a CreativeCategory object that
     * has the same id field values as this object.
     * @param o the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CreativeCategory)) {
            return false;
        }

        CreativeCategory that = (CreativeCategory)o;
        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        if (!ObjectUtils.equals(this.getDefaultName(), that.getDefaultName())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getType(), that.getType())) {
            return false;
        }

        return true;
    }

    /**
     * Returns a string representation of the object.  This implementation constructs
     * that representation based on the id fields.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "com.foros.model.creative.CreativeCategory[id=" + getId() + "]";
    }

    @XmlElement(name = "rtbCategory")
    @XmlElementWrapper(name = "rtbCategories")
    @XmlJavaTypeAdapter(RTBCategoryToKeyNameXmlAdapter.class)
    public Set<RTBCategory> getRtbCategories() {
        return new ChangesSupportSet<>(this, "rtbCategories", rtbCategories);
    }

    public void setRtbCategories(Set<RTBCategory> rtbCategories) {
        this.rtbCategories = rtbCategories;
        this.registerChange("rtbCategories");
    }
}
