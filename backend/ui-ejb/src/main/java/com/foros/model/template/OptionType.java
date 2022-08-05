package com.foros.model.template;

public enum OptionType {
    STRING("String"),
    TEXT("Text"),
    FILE("File"),
    URL("URL"),
    URL_WITHOUT_PROTOCOL("URL Without Protocol"),
    FILE_URL("File/URL"),
    INTEGER("Integer"),
    COLOR("Color"),
    ENUM("Enum"),
    HTML("HTML"),
    DYNAMIC_FILE("Dynamic File");

    private final String name;

    OptionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static OptionType byName(String name) throws IllegalArgumentException {
        for (OptionType optionType : values()) {
            if (optionType.getName().equals(name)) {
                return optionType;
            }
        }
        throw new IllegalArgumentException("Illegal name given: '" + name + "'");
    }
}
