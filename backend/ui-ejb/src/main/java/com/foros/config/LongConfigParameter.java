package com.foros.config;

public class LongConfigParameter extends AbstractConfigParameter<Long> {

    /**
     * Create required parameter
     * @param name parameter name
     */
    public LongConfigParameter(String name) {
        super(name, null , true);
    }

    /**
     * Create optional parameter
     * @param name parameter name
     * @param defaultValue default value
     */
    public LongConfigParameter(String name, Long defaultValue) {
        super(name, defaultValue, false);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    @Override
    public Long parse(String str) {
        return Long.valueOf(str);
    }
}