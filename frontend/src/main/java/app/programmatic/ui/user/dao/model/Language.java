package app.programmatic.ui.user.dao.model;

public enum Language {
    EN("en", "English"),
    JA("ja", "Japanese"),
    KO("ko", "Korean"),
    PT("pt", "Portuguese"),
    ZH("zh", "Chinese"),
    RO("ro", "Romanian"),
    TR("tr", "Turkish"),
    RU("ru", "Russian");

    private final String isoCode;
    private final String name;

    Language(String isoCode, String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return name;
    }

    public static Language valueOfCode(String isoCode) {
        for (Language language : values()) {
            if (language.getIsoCode().equals(isoCode)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Invalid language's iso code " + isoCode);
    }
}
