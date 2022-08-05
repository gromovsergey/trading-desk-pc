package app.programmatic.ui.localization.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "DYNAMICRESOURCES")
@IdClass(LocalizationId.class)
public class Localization {
    @Id
    @NotNull
    @Size(min = 1, max = 200)
    @Column(nullable = false, updatable = false)
    private String key;

    @NotNull
    @Size(min = 1, max = 1000)
    @Column(nullable = false)
    private String value;

    @NotNull
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private LocalizationLanguage lang;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalizationLanguage getLang() {
        return lang;
    }

    public void setLang(LocalizationLanguage lang) {
        this.lang = lang;
    }
}
