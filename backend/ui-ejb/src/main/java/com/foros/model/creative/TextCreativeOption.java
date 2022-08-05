package com.foros.model.creative;

import com.foros.model.template.CreativeToken;
import com.foros.model.template.OptionType;

public enum TextCreativeOption {

    HEADLINE(CreativeToken.HEADLINE.getName(), 25),
    DESCRIPTION_LINE_1(CreativeToken.DESCRIPTION1.getName(), 35),
    DESCRIPTION_LINE_2(CreativeToken.DESCRIPTION2.getName(), 35),
    DESCRIPTION_LINE_3(CreativeToken.DESCRIPTION3.getName(), 35),
    DESCRIPTION_LINE_4(CreativeToken.DESCRIPTION4.getName(), 35),
    DISPLAY_URL(CreativeToken.DISPLAY_URL.getName(), OptionType.URL, 35),
    CLICK_URL(CreativeToken.CRCLICK.getName(), OptionType.URL, 1024),
    IMAGE_FILE(CreativeToken.IMAGE_FILE.getName(), OptionType.FILE);

    private final String token;
    private final OptionType type;
    private final int length;

    private TextCreativeOption(String token, int length) {
        this(token, OptionType.STRING, length);
    }

    TextCreativeOption(String token, OptionType type) {
        this(token, type, 0);
    }

    TextCreativeOption(String token, OptionType type, int length) {
        this.token = token;
        this.type = type;
        this.length = length;
    }

    public String getToken() {
        return this.token;
    }

    public OptionType getType() {
        return type;
    }

    public static TextCreativeOption byToken(String token) {
        TextCreativeOption option = byTokenOptional(token);
        if (option == null) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }
        return option;
    }

    public static TextCreativeOption byTokenOptional(String token) {
        if (token == null) {
            return null;
        }

        for (TextCreativeOption option : values()) {
            if (option.getToken().equals(token)) {
                return option;
            }
        }
        return null;
    }

    public int getLength() {
        return length;
    }
}
