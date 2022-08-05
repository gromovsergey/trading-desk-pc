package com.foros.model.template;

import java.util.List;
import java.util.Map;

public class ImpressionInfo {
    private List<CreativeInfo> creatives;
    private Map<String, String> optionValues;

    public ImpressionInfo(Map<String, String> optionValues, List<CreativeInfo> creatives) {
        this.creatives = creatives;
        this.optionValues = optionValues;
    }

    public Map<String, String> getOptionValues() {
        return optionValues;
    }

    public List<CreativeInfo> getCreatives() {
        return creatives;
    }
}
