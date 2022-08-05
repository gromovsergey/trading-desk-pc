package com.foros.config;

public interface ConfigParameter<T> {
    String getName();
    T getDefaultValue();
    boolean isRequired();
    Class<T> getType();
    T parse(String str);
}
