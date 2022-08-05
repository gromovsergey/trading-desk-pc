package com.foros.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigImpl implements Config {

    private Map<ConfigParameter, StaticConfigValue> valueMap;
    private Map<String, ConfigParameter> parameterMap;

    public ConfigImpl(Class<?>... classes) {
        if (classes == null) {
            throw new ConfigurationException("No config classes");
        }
        valueMap = new HashMap<ConfigParameter, StaticConfigValue>();
        parameterMap = new HashMap<String, ConfigParameter>();
        for (Class<?> clazz : classes) {
            process(clazz);
        }    
    }

    @Override
    public <T> T get(ConfigParameter<T> parameter) {
        StaticConfigValue value = valueMap.get(parameter);
        if (value == null) {
            throw new ConfigurationException("Unknown parameter: " + parameter);
        }
        //noinspection unchecked
        return (T) value.getValue();
    }

    private ConfigParameter<?> getConfigParameter(Field field) {
        try {
            ConfigParameter<?> parameter = (ConfigParameter<?>) field.get(null);
            return parameter;
        } catch (IllegalAccessException e) {
            throw new ConfigurationException("Error while reading field: " + field, e);
        }
    }

    private void process(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() != ConfigParameter.class) {
                continue;
            }

            boolean valid = Modifier.isPublic(field.getModifiers())
                && Modifier.isStatic(field.getModifiers())
                && Modifier.isFinal(field.getModifiers());

            if (!valid) {
                throw new ConfigurationException("Invalid field modifiers. Must be public static final. Field: " + field);
            }


            ConfigParameter<?> parameter = getConfigParameter(field);
            if (parameterMap.containsKey(parameter.getName())) {
                throw new ConfigurationException("Duplicate parameter: " + parameter);
            }

            StaticConfigValue value = new StaticConfigValue(parameter);
            valueMap.put(parameter, value);
            parameterMap.put(parameter.getName(), parameter);
        }

        Class<?>[] classes = clazz.getDeclaredClasses();

        for (Class<?> innerClass : classes) {
            process(innerClass);
        }
    }

    Collection<ConfigParameter> getDefinitions() {
        return parameterMap.values();
    }

    private static class StaticConfigValue {
        private ConfigParameter<?> parameter;
        private Object value;

        private StaticConfigValue(ConfigParameter<?> parameter) {
            String strVal = System.getProperty(parameter.getName());
            this.parameter = parameter;
            if (strVal == null || "".equals(strVal)) {
                value = parameter.getDefaultValue();
            } else {
                value = parameter.parse(strVal);
            }

            if (value == null && parameter.isRequired()) {
                throw new ConfigurationException("Can't find value for required parameter: " + parameter);
            }
        }

        public ConfigParameter<?> getParameter() {
            return parameter;
        }

        private Object getValue() {
            return value;
        }
    }
}
