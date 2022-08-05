package com.foros.config;

public class UrlConfigParameter extends StringConfigParameter {

    /**
     * Create required parameter
     * @param name parameter name
     */
    public UrlConfigParameter(String name) {
        super(name);
    }

    /**
     * Create optional parameter
     * @param name parameter name
     * @param defaultValue default value
     */
    public UrlConfigParameter(String name, String defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public String parse(String str) {
        if (!str.endsWith("/")) {
            str += "/";
        }
        return str;
    }
}
