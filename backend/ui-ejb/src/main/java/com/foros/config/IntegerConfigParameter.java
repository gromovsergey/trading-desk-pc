package com.foros.config;

public class IntegerConfigParameter extends AbstractConfigParameter<Integer> {

    /**
     * Create required parameter
     * @param name parameter name
     */
    public IntegerConfigParameter(String name) {
        super(name, null , true);
    }

    /**
     * Create optional parameter
     * @param name parameter name
     * @param defaultValue default value
     */
    public IntegerConfigParameter(String name, Integer defaultValue) {
        super(name, defaultValue, false);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Integer parse(String str) {
        return Integer.valueOf(str);
    }
}