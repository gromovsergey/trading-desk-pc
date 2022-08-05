package com.foros.model.template;

public enum TemplateFileType {
    TEXT('T', "Text"),
    XSLT('X', "XSLT");

    private final char letter;
    private final String description;

    private TemplateFileType(char letter, String description) {
        this.letter = letter;
        this.description = description;
    }

    public char getLetter() {
        return letter;
    }

    public String getDescription() {
        return description;
    }

    public static TemplateFileType valueOf(char letter) throws IllegalArgumentException {
        switch (letter) {
            case 'T':
                return TEXT;
            case 'X':
                return XSLT;
            default:
                throw new IllegalArgumentException("Illegal letter given: '" + letter + "'");
        }
    }
}
