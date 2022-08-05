package com.foros.util.preview;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PreviewModel {
    private Map<String, TokenDefinition> impressionDefinitions;
    private Map<String, TokenDefinition> creativeDefinitions;

    public PreviewModel(Map<String, TokenDefinition> impressionDefinitions, Map<String, TokenDefinition> creativeDefinitions) {
        this.impressionDefinitions = impressionDefinitions;
        this.creativeDefinitions = creativeDefinitions;
    }

    public Map<String, TokenDefinition> getCreativeDefinitions() {
        return Collections.unmodifiableMap(creativeDefinitions);
    }

    public Map<String, TokenDefinition> getImpressionDefinitions() {
        return Collections.unmodifiableMap(impressionDefinitions);
    }

    public Map<String, TokenDefinition> getAllDefinitions() {
        Map<String, TokenDefinition> all = new HashMap<>();
        all.putAll(creativeDefinitions);
        all.putAll(impressionDefinitions);
        return all;
    }
}
