package com.foros.model.admin;

import com.foros.util.HashUtil;
import java.io.Serializable;

public class DynamicResourceId implements Serializable {
    private String key;
    private String lang;

    public DynamicResourceId() {
    }

    public DynamicResourceId(String key, String lang) {
        this.key = key;
        this.lang = lang;
    }

    public String getKey() {
        return key;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof DynamicResourceId) &&
            key.equals(((DynamicResourceId)o).getKey()) &&
            lang.equals(((DynamicResourceId)o).getLang()));
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(key, lang);
    }
}
