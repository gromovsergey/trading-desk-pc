package com.foros.action.creative.display;

import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.template.Option;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OptionValueTO {
    private Long optionId;
    private String value;
    private Timestamp version;

    public OptionValueTO() {
    }

    public OptionValueTO(CreativeOptionValue optionValue) {
        optionId = optionValue.getOption().getId();
        version = optionValue.getVersion();
        value = optionValue.getValue();
    }

    public OptionValueTO(Long optionId, String value, Timestamp version) {
        this.optionId = optionId;
        this.version = version;
        this.value = value;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public CreativeOptionValue toEntity() {
        CreativeOptionValue cov = new CreativeOptionValue();
        cov.setOption(new Option(getOptionId()));
        cov.setValue(getValue());
        cov.setVersion(getVersion());
        return cov;
    }

    public static List<OptionValueTO> valueOf(Creative creative) {
        Set<CreativeOptionValue> optionValues = creative.getOptions();
        List<OptionValueTO> tos = new ArrayList<OptionValueTO>(optionValues.size());
        for (CreativeOptionValue optionValue : optionValues) {
            tos.add(new OptionValueTO(optionValue));
        }
        return tos;
    }
}
