package com.foros.action;

import com.foros.util.StringUtil;

public class LanguageBean implements Comparable<LanguageBean> {
    private String code;

    private String name;

    public LanguageBean() {}

    public LanguageBean(String languageCode) {
        this.code = languageCode;
        this.name = StringUtil.resolveGlobal("language", languageCode, true);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(LanguageBean that) {
        return name.compareTo(that.getName());
    }
}
