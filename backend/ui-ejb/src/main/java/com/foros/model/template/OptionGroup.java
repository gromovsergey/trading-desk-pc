package com.foros.model.template;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.model.creative.CreativeSize;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "OPTIONGROUP")
@XmlType(propOrder = {
        "id",
        "defaultName",
        "defaultLabel",
        "availability",
        "collapsability",
        "type",
        "options"
})
@XmlAccessorType(XmlAccessType.NONE)
public class OptionGroup extends VersionEntityBase implements LocalizableNameEntity {

    @SequenceGenerator(name = "OptionGroupSeq", sequenceName = "OPTIONGROUP_OPTION_GROUP_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OptionGroupSeq")
    @IdConstraint
    @Column(name = "OPTION_GROUP_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "NAME", nullable = false)
    private String defaultName;

    @StringSizeConstraint(size = 50)
    @Column(name = "LABEL")
    private String defaultLabel;

    @RequiredConstraint
    @Column(name = "AVAILABILITY", nullable = false)
    private String availability = "A";

    @RequiredConstraint
    @Column(name = "COLLAPSIBILITY", nullable = false)
    private String collapsability = "N";

    @JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID", updatable = false, nullable = true)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private CreativeSize creativeSize;


    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID", updatable = false, nullable = true)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Template template;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "optionGroup", cascade = {CascadeType.ALL})
    @OrderBy("sortOrder")
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<Option> options = new LinkedHashSet<Option>();

    @RequiredConstraint
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, updatable = false)
    private OptionGroupType type;

    @Column(name = "SORT_ORDER")
    private Long sortOrder;

    public OptionGroup() {
    }

    public OptionGroup(Long id) {
        this.id = id;
    }

    @XmlElement
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
    public LocalizableName getName() {
        return LocalizableNameProvider.OPTION_GROUP.provide(getDefaultName(), getId());
    }

    @XmlElement
    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        this.registerChange("defaultName");
    }

    public LocalizableName getLabel() {
       return new LocalizableName(getDefaultLabel(), "OptionGroup-label." + getId());
    }

    @XmlElement
    public String getDefaultLabel() {
        return defaultLabel;
    }

    public void setDefaultLabel(String defaultLabel) {
        this.defaultLabel = defaultLabel;
        this.registerChange("defaultLabel");
    }

    @XmlElement
    public Availability getAvailability() {
        return Availability.byName(this.availability);
    }

    public void setAvailability(Availability availability) {
        this.availability = availability.getName();
        this.registerChange("availability");
    }

    @XmlElement(name = "collapsibility")
    public Collapsability getCollapsability() {
        return Collapsability.byName(this.collapsability);
    }

    public void setCollapsability(Collapsability collapsability) {
        this.collapsability = collapsability.getName();
        this.registerChange("collapsability");
    }

    public CreativeSize getCreativeSize() {
        return creativeSize;
    }

    public void setCreativeSize(CreativeSize creativeSize) {
        this.creativeSize = creativeSize;
        this.registerChange("creativeSize");
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
        this.registerChange("template");
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
        this.registerChange("sortOrder");
    }

    @XmlElement
    public OptionGroupType getType() {
        return type;
    }

    public void setType(OptionGroupType type) {
        this.type = type;
        this.registerChange("type");
    }

    @XmlElement(name = "option")
    @XmlElementWrapper(name = "options")
    public Set<Option> getOptions() {
        return new ChangesSupportSet<Option>(this, "options", options);
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
        this.registerChange("options");
    }

    @Override
    public String toString() {
        return "com.foros.model.template.OptionGroup[id=" + getId() + "]";
    }

    public enum Availability {
        ALWAYS_ENABLED("A"),
        ENABLED_BY_DEFAULT("E"),
        DISABLED_BY_DEFAULT("D");

        private final String name;

        private Availability(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Availability byName(String name) throws IllegalArgumentException {
            if ("A".equals(name)) {
                return ALWAYS_ENABLED;
            } else if ("E".equals(name)) {
                return ENABLED_BY_DEFAULT;
            } else if ("D".equals(name)) {
                return DISABLED_BY_DEFAULT;
            }

            throw new IllegalArgumentException("Illegal name given: '" + name + "'");
        }
    }

    public enum Collapsability {
        NOT_COLLAPSIBLE("N"),
        COLLAPSED_BY_DEFAULT("C"),
        EXPANDED_BY_DEFAULT("E");

        private final String name;

        private Collapsability(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Collapsability byName(String name) throws IllegalArgumentException {
            if ("N".equals(name)) {
                return NOT_COLLAPSIBLE;
            } else if ("C".equals(name)) {
                return COLLAPSED_BY_DEFAULT;
            } else if ("E".equals(name)) {
                return EXPANDED_BY_DEFAULT;
            }

            throw new IllegalArgumentException("Illegal name given: '" + name + "'");
        }
    }
}
