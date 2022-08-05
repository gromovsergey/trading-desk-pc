package com.foros.model.template;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "OPTIONENUMVALUE")
public class OptionEnumValue extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "OptionEnumValueGen", sequenceName = "OPTIONENUMVALUE_OPTION_ENUM_VALUE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OptionEnumValueGen")
    @Column(name = "OPTION_ENUM_VALUE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @JoinColumn(name = "OPTION_ID", referencedColumnName = "OPTION_ID", updatable = false, nullable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Option option;

    @Column(name = "IS_DEFAULT", nullable = false)
    private boolean isDefault;

    @Column(name = "VALUE", nullable = false)
    private String value;

    @Column(name = "NAME", nullable = false)
    private String name;

    public OptionEnumValue() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
        this.registerChange("option");
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        this.isDefault = aDefault;
        this.registerChange("isDefault");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.registerChange("value");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }
}
