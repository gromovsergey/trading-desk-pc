package com.foros.model.template;

import java.util.HashMap;
import java.util.Map;

public class CreativeInfo {
    private Map<String, String> optionValues;

    public CreativeInfo() {
        this(new HashMap<String, String>());
    }

    public CreativeInfo(Map<String, String> optionValues) {
        this.optionValues = optionValues;
    }

    public Map<String, String> getOptionValues() {
        return optionValues;
    }

    public void setOptionValues(Map<String, String> optionValues) {
        this.optionValues = optionValues;
    }
}
