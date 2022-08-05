package com.foros.config;

public abstract class AbstractConfigParameter<T> implements ConfigParameter<T> {
    private String name;
    private T defaultValue;
    private boolean required;

    protected AbstractConfigParameter(String name, T defaultValue, boolean required) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.required = required;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "Name=" + getName() + ", Type=" + getType().getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractConfigParameter that = (AbstractConfigParameter) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
