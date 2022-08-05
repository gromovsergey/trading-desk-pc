package app.programmatic.ui.localization.dao.model;

public enum LocalizationObjectKey {
    OPTION_NAME("Option-name."),
    OPTION_LABEL("Option-label."),
    OPTION_GROUP_NAME("OptionGroup-name."),
    OPTION_GROUP_LABEL("OptionGroup-label."),
    CATEGORY("CreativeCategory."),
    TEMPLATE("Template."),
    SIZE("CreativeSize."),
    CHANNEL("Channel.");

    private String prefix;

    LocalizationObjectKey(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
