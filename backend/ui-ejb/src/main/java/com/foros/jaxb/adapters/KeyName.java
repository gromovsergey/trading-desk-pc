package com.foros.jaxb.adapters;

public class KeyName {
    private String key;
    private String name;

    public KeyName() {
    }

    public KeyName(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
