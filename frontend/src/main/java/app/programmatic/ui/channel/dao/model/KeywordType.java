package app.programmatic.ui.channel.dao.model;

public enum KeywordType {
    PAGE_KEYWORD("P", "trigger.type.page"),
    SEARCH_KEYWORD("S", "trigger.type.search"),
    URL("U", "trigger.type.url"),
    URL_KEYWORD("R", "trigger.type.urlkeyword");

    private String letter;
    private String descriptionKey;

    KeywordType(String letter, String descriptionKey) {
        this.letter = letter;
        this.descriptionKey = descriptionKey;
    }

    public String getLetter() {
        return letter;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }
}
