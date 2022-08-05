package com.foros.framework.struts2validator;

public class RegexDefs {
    public static final String ONLY_HTML_SYMBOLS = "^[^<>&]*$";
    public static final String MAX_INT = "2147483647";
    public static final String LATIN_LETTERS = "[\\w- ]*";
    public static final String ONLY_COLOR_LETTERS = "^[1234567890ABCDEFabcdef]{6}+$";
    public static final String ONLY_LETTERS_OR_DIGITS="^[a-zA-Z0-9]*$";
}
