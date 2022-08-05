package com.foros.action.admin.option;

import com.foros.util.UITimestamp;

public class CachedOptionValue {
    private Long optionId;

    private String value;

    private UITimestamp version;

    public CachedOptionValue() {
    }

    public CachedOptionValue(Long optionId, String value, UITimestamp version) {
        this.optionId = optionId;
        this.value = value;
        this.version = version;
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

    public UITimestamp getVersion() {
        return version;
    }

    public void setVersion(UITimestamp version) {
        this.version = version;
    }
}
