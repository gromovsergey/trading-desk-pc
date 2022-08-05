package com.foros.action.site;

import com.foros.util.UITimestamp;

public class OptionValueForm {
    private String name;
    private UITimestamp version;
    private String value;
    private String type;
    private Long optionId;
    private String token;
    private Long enumValueId;

    public OptionValueForm() {
    }
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public UITimestamp getVersion() {
        return version;
    }

    public void setVersion(UITimestamp version) {
        this.version = version;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getEnumValueId() {
        return enumValueId;
    }

    public void setEnumValueId(Long enumValueId) {
        this.enumValueId = enumValueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
