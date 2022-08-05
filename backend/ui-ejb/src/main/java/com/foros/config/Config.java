package com.foros.config;

public interface Config {
    <T> T get(ConfigParameter<T> parameter);
}
