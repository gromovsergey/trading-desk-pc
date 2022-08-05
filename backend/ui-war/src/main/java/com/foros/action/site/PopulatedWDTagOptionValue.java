package com.foros.action.site;

import com.foros.action.IdNameBean;
import com.foros.model.site.WDTagOptionValue;
import com.foros.model.template.Option;
import com.foros.model.template.OptionEnumValue;
import com.foros.model.template.OptionType;
import com.foros.util.LocalizableNameUtil;
import com.foros.util.StringUtil;

import java.sql.Timestamp;
import java.util.List;

public class PopulatedWDTagOptionValue implements Comparable<PopulatedWDTagOptionValue> {
    private String name;
    private String label;
    private String optionId;
    private long sortOrder;
    private OptionType type;
    private String value;
    private List<IdNameBean> availableValues;
    private boolean mandatory;
    private Timestamp version;
    private List<String> fileTypes;

    PopulatedWDTagOptionValue() {
    }

    public PopulatedWDTagOptionValue(WDTagOptionValue option) {
        name = LocalizableNameUtil.getLocalizedValue(option.getOption().getName());

        if (StringUtil.isPropertyNotEmpty(option.getOption().getDefaultLabel())) {
            label = LocalizableNameUtil.getLocalizedValue(option.getOption().getLabel());
        }

        optionId = option.getOption().getId().toString();
        type = option.getOption().getType();
        value = option.getValue();
        mandatory = option.getOption().isRequired();
        version = option.getVersion();

        if (type == OptionType.ENUM) {
            availableValues = WDTagActionHelper.prepareEnumValues(option.getOption());
        } else if (type == OptionType.FILE || type == OptionType.DYNAMIC_FILE || type == OptionType.FILE_URL) {
            fileTypes = WDTagActionHelper.prepareFileTypes(option.getOption()); 
        }
    }

    public PopulatedWDTagOptionValue(Option option) {
        name = LocalizableNameUtil.getLocalizedValue(option.getName());

        if (StringUtil.isPropertyNotEmpty(option.getDefaultLabel())) {
            label = LocalizableNameUtil.getLocalizedValue(option.getLabel());
        }

        optionId = option.getId().toString();
        sortOrder = option.getSortOrder();
        type = option.getType();
        mandatory = option.isRequired();

        if (type == OptionType.ENUM) {
            availableValues = WDTagActionHelper.prepareEnumValues(option);

            for (OptionEnumValue val : option.getValues()) {
                if (val.isDefault()) {
                    value = val.getId().toString();
                    break;
                }
            }
        } else {
            value = option.getDefaultValue();
        }

        if (type == OptionType.FILE || type == OptionType.DYNAMIC_FILE || type == OptionType.FILE_URL) {
            fileTypes = WDTagActionHelper.prepareFileTypes(option);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public OptionType getType() {
        return type;
    }

    public void setType(OptionType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<IdNameBean> getAvailableValues() {
        return availableValues;
    }

    public void setAvailableValues(List<IdNameBean> availableValues) {
        this.availableValues = availableValues;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Timestamp getVersion() {
        if (version == null) {
            version = new Timestamp(System.currentTimeMillis());
        }
        return version;
    }

    public void setVersion(Timestamp version) {
        if (version == null) {
            this.version = new Timestamp((System.currentTimeMillis()));
        } else {
            this.version = version;
        }
    }

    public List<String> getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    @Override
    public int compareTo(PopulatedWDTagOptionValue o) {
        long diff = this.getSortOrder() - o.getSortOrder();

        return diff != 0 ? (int) diff :
                StringUtil.compareToIgnoreCase(this.getName(), o.getName());
    }
}
