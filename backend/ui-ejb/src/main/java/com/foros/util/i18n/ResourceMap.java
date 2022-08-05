package com.foros.util.i18n;

import com.foros.model.admin.DynamicResource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ResourceMap implements Serializable {

    private HashMap<String, String> lookup = new HashMap<String, String>();

    public ResourceMap(List<DynamicResource> resources) {
        for (DynamicResource res : resources) {
            lookup.put(res.getKey(), res.getValue());
        }
    }

    public String getString(String key) {
        return lookup.get(key);
    }

    public Set<String> getKeys() {
        return lookup.keySet();
    }
}
