package com.foros.model.template;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "TEMPLATE")
@Inheritance
@DiscriminatorColumn(name = "TEMPLATE_TYPE")
@NamedQueries({
    @NamedQuery(name = "Template.findAll", query = "SELECT t FROM Template t "),
    @NamedQuery(name = "Template.findAllNotDeleted", query = "SELECT t FROM Template t WHERE NOT t.status = 'D' "),
    @NamedQuery(name = "Template.findById", query = "SELECT t FROM Template t WHERE t.id = :id"),
    @NamedQuery(name = "Template.findByStatus", query = "SELECT t FROM Template t WHERE t.status = :status"),
    @NamedQuery(name = "Template.findByAccType", query = "SELECT distinct t FROM AccountType a, in (a.templates) t WHERE t.status <> 'D' and a = :accType")
})
@AllowedStatuses(values = { Status.ACTIVE, Status.DELETED })
@XmlType(propOrder = {
        "id",
        "defaultName",
        "templateFiles",
        "optionGroups",
})
public abstract class Template extends StatusEntityBase implements LocalizableNameEntity, DisplayStatusEntity {
    public static final String TEXT_TEMPLATE = "Text";

    @SequenceGenerator(name = "TemplateGen", sequenceName = "TEMPLATE_TEMPLATE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TemplateGen")
    @Column(name = "TEMPLATE_ID", nullable = false)
    @IdConstraint
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @NameConstraint
    @RequiredConstraint
    private String defaultName;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "template", cascade = {CascadeType.ALL})
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<TemplateFile> templateFiles = new LinkedHashSet<TemplateFile>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "template", cascade = {CascadeType.ALL})
    @OrderBy("sortOrder")
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<OptionGroup> optionGroups = new LinkedHashSet<OptionGroup>();

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "template.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "template.displaystatus.deleted");

    public Template() {
        this.status = Status.ACTIVE.getLetter();
    }

    public Template(Long id) {
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
        return LocalizableNameProvider.TEMPLATE.provide(getDefaultName(), getId());
    }

    @XmlElement
    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
        this.registerChange("defaultName");
    }

    @XmlElement(name = "templateFile")
    @XmlElementWrapper(name = "templateFiles")
    public Set<TemplateFile> getTemplateFiles() {
        return new ChangesSupportSet<TemplateFile>(this, "templateFiles", templateFiles);
    }

    public void setTemplateFiles(Set<TemplateFile> templateFiles) {
        this.templateFiles = templateFiles;
        this.registerChange("templateFiles");
    }

    public boolean isText(){
        return isTextName(getName());
    }

    public static boolean isTextName(LocalizableName templateName) {
        return TEXT_TEMPLATE.equals(templateName.getDefaultName());
    }

    @Transient
    public Collection<Option> getAllOptions() {
        ArrayList<Option> options = new ArrayList<Option>();
        for (OptionGroup optionGroup: getOptionGroups()) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    private Set<OptionGroup> getOptionsByGroupType(OptionGroupType groupType) {
        Set<OptionGroup> groups = new LinkedHashSet<OptionGroup>();
        for (OptionGroup optionGroup: getOptionGroups()) {
            if (groupType == optionGroup.getType()) {
                groups.add(optionGroup);
            }
        }
        return new ChangesSupportSet<OptionGroup>(this, "optionGroups", groups);
    }

    public Set<OptionGroup> getPublisherOptionGroups() {
        return getOptionsByGroupType(OptionGroupType.Publisher);
    }

    public Collection<Option> getPublisherOptions() {
        Collection<Option> options = new ArrayList<Option>();
        for (OptionGroup optionGroup : getPublisherOptionGroups()) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    public Collection<Option> getAdvertiserOptions() {
        Collection<Option> options = new ArrayList<Option>();
        for (OptionGroup optionGroup : getAdvertiserOptionGroups()) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    public Set<OptionGroup> getAdvertiserOptionGroups() {
        return getOptionsByGroupType(OptionGroupType.Advertiser);
    }

    public Collection<Option> getHiddenOptions() {
        Collection<Option> options = new ArrayList<Option>();
        for (OptionGroup optionGroup : getHiddenOptionGroups()) {
            options.addAll(optionGroup.getOptions());
        }
        return options;
    }

    public Set<OptionGroup> getHiddenOptionGroups() {
        return getOptionsByGroupType(OptionGroupType.Hidden);
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Template)) {
            return false;
        }

        Template other = (Template)object;

        if (!ObjectUtils.equals(id, other.id)) {
            return false;
        }

        return true;
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
        Template template = option.getOptionGroup().getTemplate();
        return template == null ? false : template.getId().equals(getId());
    }
}
