package com.foros.config;

public class StringConfigParameter extends AbstractConfigParameter<String> {

    /**
     * Create required parameter
     * @param name parameter name
     */
    public StringConfigParameter(String name) {
        super(name, null , true);
    }

    /**
     * Create optional parameter
     * @param name parameter name
     * @param defaultValue default value
     */
    public StringConfigParameter(String name, String defaultValue) {
        super(name, defaultValue, false);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String parse(String str) {
        return str;
    }
}
