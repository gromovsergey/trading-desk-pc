package app.programmatic.ui.localization.dao.model;

import java.io.Serializable;

public class LocalizationId implements Serializable {
    private String key;
    private LocalizationLanguage lang;

    public LocalizationId() {
    }

    public LocalizationId(String key, LocalizationLanguage lang) {
        this.key = key;
        this.lang = lang;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LocalizationLanguage getLang() {
        return lang;
    }

    public void setLang(LocalizationLanguage lang) {
        this.lang = lang;
    }

    @Override
    public String toString() {
        return "\"id{" +
                "key='" + key + '\'' +
                ", lang=" + lang +
                "}\"";
    }
}
