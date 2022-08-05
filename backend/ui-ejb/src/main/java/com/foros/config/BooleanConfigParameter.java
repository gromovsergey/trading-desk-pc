package com.foros.config;

public class BooleanConfigParameter extends AbstractConfigParameter<Boolean> {

    /**
     * Create required parameter
     * @param name parameter name
     */
    public BooleanConfigParameter(String name) {
        super(name, null , true);
    }

    /**
     * Create optional parameter
     * @param name parameter name
     * @param defaultValue default value
     */
    public BooleanConfigParameter(String name, Boolean defaultValue) {
        super(name, defaultValue, false);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public Boolean parse(String str) {
        return Boolean.valueOf(str);
    }
}