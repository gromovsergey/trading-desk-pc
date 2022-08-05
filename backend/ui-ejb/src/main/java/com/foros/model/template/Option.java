package com.foros.model.template;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.OptionEnumValueXmlAdapter;
import com.foros.jaxb.adapters.OptionFileTypeAdapter;
import com.foros.model.LocalizableName;
import com.foros.model.LocalizableNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.util.changes.ChangesSupportList;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.i18n.LocalizableNameProvider;
import com.foros.validation.annotation.Validator;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RangeConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;
import com.foros.validation.constraint.validator.ValidatorFactories;

import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "OPTIONS")
@NamedQueries({
    @NamedQuery(name = "Option.findAll", query = "SELECT o FROM Option o"),
    @NamedQuery(name = "Option.findById", query = "SELECT o FROM Option o WHERE o.id = :id"),
    @NamedQuery(name = "Option.findByType", query = "SELECT o FROM Option o WHERE o.type = :type"),
    @NamedQuery(name = "Option.findByToken", query = "SELECT o FROM Option o WHERE o.token = :token and o.optionGroup.template.defaultName = :templateName"),
    @NamedQuery(name = "Option.findByDefaultValue", query = "SELECT o FROM Option o WHERE o.defaultValue = :defaultValue")
})
@XmlType(propOrder = {
        "id",
        "defaultName",
        "defaultLabel",
        "type",
        "token",
        "defaultValue",
        "genericTokensFlag",
        "advertiserTokensFlag",
        "publisherTokensFlag",
        "internalTokensFlag",
        "required",
        "internalUse",
        "minValue",
        "maxValue",
        "maxLength",
        "maxLengthFullWidth",
        "fileTypes",
        "values"
})
@XmlAccessorType(XmlAccessType.NONE)
public class Option extends VersionEntityBase implements LocalizableNameEntity {

    public static final long GENERIC = 0x01;
    public static final long ADVERTISER = 0x02;
    public static final long PUBLISHER = 0x04;
    public static final long INTERNAL = 0x08;

    private static final OptionType[] allowedTypes = {
            OptionType.STRING,
            OptionType.TEXT,
            OptionType.FILE,
            OptionType.URL,
            OptionType.URL_WITHOUT_PROTOCOL,
            OptionType.FILE_URL,
            OptionType.INTEGER,
            OptionType.COLOR,
            OptionType.ENUM,
            OptionType.HTML,
            OptionType.DYNAMIC_FILE
    };

    @SequenceGenerator(name = "OptionsGen", sequenceName = "OPTIONS_OPTION_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OptionsGen")
    @IdConstraint
    @Column(name = "OPTION_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @RequiredConstraint
    @NameConstraint
    @Column(name = "NAME", nullable = false)
    private String defaultName;

    @StringSizeConstraint(size = 1000)
    @Column(name = "LABEL")
    private String defaultLabel;

    @RequiredConstraint
    @Column(name = "TYPE", nullable = false)
    private String type = "String";

    @RequiredConstraint
    @Validator(factory = ValidatorFactories.OptionToken.class)
    @StringSizeConstraint(size = 50)
    @Column(name = "TOKEN")
    private String token;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    @Column(name = "RECURSIVE_TOKENS")
    private Long recursiveTokens;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "option", cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @OrderBy(value = "value")
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    private Set<OptionEnumValue> values = new LinkedHashSet<OptionEnumValue>();

    @JoinColumn(name = "OPTION_GROUP_ID", referencedColumnName = "OPTION_GROUP_ID", updatable = true, nullable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private OptionGroup optionGroup;

    @Column(name = "REQUIRED", nullable = false)
    private boolean required;

    @Column(name = "IS_INTERNAL", nullable = false)
    private boolean internalUse;

    @RangeConstraint(min = "-9999999999", max = "9999999999")
    @Column(name = "MIN_VALUE")
    private Long minValue;

    @RangeConstraint(min = "-9999999999", max = "9999999999")
    @Column(name = "MAX_VALUE")
    private Long maxValue;

    @RangeConstraint(min = "1", max = "2000")
    @Column(name = "MAX_LENGTH")
    private Long maxLength;

    @RangeConstraint(min = "1", max = "2000")
    @Column(name = "MAX_LENGTH_FULLWIDTH")
    private Long maxLengthFullWidth;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "option", cascade = {CascadeType.ALL})
    @OrderBy(value = "fileType")
    @Fetch(FetchMode.SUBSELECT)
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedList.class)
    private List<OptionFileType> fileTypes = new LinkedList<OptionFileType>();

    @Column(name = "SORT_ORDER")
    private Long sortOrder;

    public Option() {
    }

    public Option(String token) {
        this.token = token;
    }

    public Option(Long id) {
        this.id = id;
    }

    public Option(Long id, LocalizableName name, OptionType type) {
        this.id = id;
        this.defaultName = name.getDefaultName();
        this.type = type.getName();
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
        return LocalizableNameProvider.OPTION.provide(getDefaultName(), getId());
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
       return new LocalizableName(getDefaultLabel(), "Option-label." + getId());
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
    public OptionType getType() {
        return OptionType.byName(this.type);
    }

    public void setType(OptionType type) {
        this.type = type.getName();
        this.registerChange("type");
    }

    public static OptionType[] getAllowedTypes() {
        return allowedTypes;
    }

    @XmlElement
    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
        this.registerChange("token");
    }

    @XmlElement
    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        this.registerChange("defaultValue");
    }

    public Long getRecursiveTokens() {
        return this.recursiveTokens == null ? 0 : this.recursiveTokens;
    }

    public void setRecursiveTokens(Long recursiveTokens) {
        this.recursiveTokens = recursiveTokens;
        this.registerChange("recursiveTokens");
    }

    @XmlElement(name = "genericTokensFlag")
    public boolean isGenericTokensFlag() {
        return (getRecursiveTokens() & GENERIC) != 0;
    }

    public void setGenericTokensFlag(boolean flag) {
        if (flag) {
            this.setRecursiveTokens(this.getRecursiveTokens() | GENERIC);
        } else {
            this.setRecursiveTokens(this.getRecursiveTokens() & ~GENERIC);
        }
    }

    @XmlElement(name = "advertiserTokensFlag")
    public boolean isAdvertiserTokensFlag() {
        return (getRecursiveTokens() & ADVERTISER) != 0;
    }

    public void setAdvertiserTokensFlag(boolean flag) {
        if (flag) {
            this.setRecursiveTokens(this.getRecursiveTokens() | ADVERTISER);
        } else {
            this.setRecursiveTokens(this.getRecursiveTokens() & ~ADVERTISER);
        }
    }

    @XmlElement(name = "publisherTokensFlag")
    public boolean isPublisherTokensFlag() {
        return (getRecursiveTokens() & PUBLISHER) != 0;
    }

    public void setPublisherTokensFlag(boolean flag) {
        if (flag) {
            this.setRecursiveTokens(this.getRecursiveTokens() | PUBLISHER);
        } else {
            this.setRecursiveTokens(this.getRecursiveTokens() & ~PUBLISHER);
        }
    }

    @XmlElement(name = "internalTokensFlag")
    public boolean isInternalTokensFlag() {
        return (getRecursiveTokens() & INTERNAL) != 0;
    }

    public void setInternalTokensFlag(boolean flag) {
        if (flag) {
            this.setRecursiveTokens(this.getRecursiveTokens() | INTERNAL);
        } else {
            this.setRecursiveTokens(this.getRecursiveTokens() & ~INTERNAL);
        }
    }

    @XmlElement
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
        this.registerChange("required");
    }

    @XmlElement
    public Long getMinValue() {
        return minValue;
    }

    public void setMinValue(Long minValue) {
        this.minValue = minValue;
        this.registerChange("minValue");
    }

    @XmlElement
    public Long getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
        this.registerChange("maxValue");
    }

    @XmlElement
    public Long getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Long maxLength) {
        this.maxLength = maxLength;
        this.registerChange("maxLength");
    }

    @XmlElement
    public Long getMaxLengthFullWidth() {
        return maxLengthFullWidth;
    }

    public void setMaxLengthFullWidth(Long maxLengthFullWidth) {
        this.maxLengthFullWidth = maxLengthFullWidth;
        this.registerChange("maxLengthFullWidth");
    }

    @XmlElement(name = "enumValue")
    @XmlElementWrapper(name = "enumValues")
    @XmlJavaTypeAdapter(OptionEnumValueXmlAdapter.class)
    public Set<OptionEnumValue> getValues() {
        return new ChangesSupportSet<OptionEnumValue>(this, "values", values);
    }

    public void setValues(Set<OptionEnumValue> values) {
        this.values = values;
        this.registerChange("values");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (id != null ? !id.equals(option.id) : option.id != null) return false;
        if (token != null ? !token.equals(option.token) : option.token != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "com.foros.model.template.Option[id=" + getId() + ", token=" + getToken() + "]";
    }

    public List<String> getFileTypesStrings() {
        List<String> fileTypesStrings = new ArrayList<String>();

        for (OptionFileType fileType : fileTypes) {
            fileTypesStrings.add(fileType.getFileType());
        }

        return fileTypesStrings;
    }

    @XmlElement(name = "fileType")
    @XmlElementWrapper(name = "fileTypes")
    @XmlJavaTypeAdapter(OptionFileTypeAdapter.class)
    public List<OptionFileType> getFileTypes() {
        return new ChangesSupportList<OptionFileType>(this, "fileTypes", fileTypes);
    }

    public void setFileTypes(List<OptionFileType> fileTypes) {
        this.fileTypes = fileTypes;
        this.registerChange("fileTypes");
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
        this.registerChange("sortOrder");
    }

    public OptionGroup getOptionGroup() {
        return optionGroup;
    }

    public void setOptionGroup(OptionGroup optionGroup) {
        this.optionGroup = optionGroup;
        this.registerChange("optionGroup");
    }

    @XmlElement
    public boolean isInternalUse() {
        return internalUse;
    }

    public void setInternalUse(boolean internalUse) {
        this.internalUse = internalUse;
        this.registerChange("internalUse");
    }

    public Set<String> buildSubstitutionTokens() {
        Set<SubstitutionCategory> categories = new HashSet<SubstitutionCategory>() {
            {
                add(SubstitutionCategory.GENERIC);
                add(SubstitutionCategory.ADVERTISER);
                add(SubstitutionCategory.PUBLISHER);
                add(SubstitutionCategory.INTERNAL);
            }
        };

        Set<String> result = new HashSet<>();
        for (SubstitutionCategory category : categories) {
            for (CreativeToken token : category.getTokens()) {
                result.add(token.getName());
            }
        }

        return result;
    }

}
