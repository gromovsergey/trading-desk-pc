package com.foros.model;

import com.foros.util.StringUtil;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.apache.commons.lang.ObjectUtils;

@Embeddable
public class LocalizableName implements Serializable {
    @Column(name = "NAME")
    private String defaultName;

    @Column(name = "NAME_KEY")
    private String resourceKey;

    public LocalizableName() {
    }

    public LocalizableName(String defaultName) {
        this.defaultName = defaultName;
    }

    public LocalizableName(String defaultName, String resourceKey) {
        this(defaultName);
        this.resourceKey = resourceKey;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public void setResourceKey(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public String toString() {
        return "LocalizableName[key=" + resourceKey + "; default=" + defaultName + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizableName that = (LocalizableName) o;

        if (!ObjectUtils.defaultIfNull(defaultName, "").equals(ObjectUtils.defaultIfNull(that.defaultName, ""))) {
            return false;
        }
        if (!ObjectUtils.defaultIfNull(resourceKey, "").equals(ObjectUtils.defaultIfNull(that.resourceKey, ""))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = StringUtil.isPropertyEmpty(defaultName) ? 0 : defaultName.hashCode();
        result = 31 * result + (StringUtil.isPropertyEmpty(resourceKey) ? 0 : resourceKey.hashCode());
        return result;
    }
}
