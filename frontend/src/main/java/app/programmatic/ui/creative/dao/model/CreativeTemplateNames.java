package app.programmatic.ui.creative.dao.model;

public enum CreativeTemplateNames {
    IMAGE("Image (RU)"),
    HTML("IFRAME (RU)");

    private final String name;

    CreativeTemplateNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
