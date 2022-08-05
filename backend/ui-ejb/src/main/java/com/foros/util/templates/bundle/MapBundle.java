package com.foros.util.templates.bundle;

import java.util.Map;

/**
 * Author: Boris Vanin
 */
class MapBundle implements Bundle {

    private Map<String, String> bundle;

    public MapBundle(Map<String, String> bundle) {
        this.bundle = bundle;
    }

    public String get(String key) {
        return bundle.get(key);
    }

}