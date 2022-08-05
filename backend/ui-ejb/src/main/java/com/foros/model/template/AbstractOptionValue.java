package com.foros.model.template;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.VersionEntityBase;
import com.foros.util.url.URLValidator;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractOptionValue extends VersionEntityBase implements OptionValue {

    @Column(name = "VALUE")
    private String value;

    @ManyToOne
    @JoinColumn(name = "OPTION_ID", referencedColumnName = "OPTION_ID", insertable = false, updatable = false)
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Option option;

    @Override
    public boolean isUrl() {
        if (getOption().getType() == OptionType.URL) {
            return true;
        }

        if (getOption().getType() == OptionType.FILE_URL &&
                URLValidator.isValid(URLValidator.urlForValidate(getValue()))) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isFile() {
        return OptionValueUtils.isFile(this);
    }

    @Override
    public String getFileStripped() {
        return OptionValueUtils.getFileStripped(this);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
        this.registerChange("value");
    }

    @Override
    public Option getOption() {
        return this.option;
    }

    @Override
    public void setOption(Option option) {
        this.option = option;
        this.registerChange("option");
    }

}
