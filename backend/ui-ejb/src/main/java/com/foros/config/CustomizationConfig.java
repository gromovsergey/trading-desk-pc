package com.foros.config;


import com.foros.util.customization.CustomizationHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CustomizationConfig implements Config {
    private static final Object NULL = new Object();

    private Map<ConfigParameter, Object> values = new HashMap<>();

    public CustomizationConfig(String customizationName, ConfigImpl config) {

        Properties allProperties = readProperties(customizationName, config);

        for (ConfigParameter configParameter : config.getDefinitions()) {
            String name = configParameter.getName();
            String strValue = allProperties.getProperty(name);
            Object value;
            if (strValue == null || "".equals(strValue)) {
                value = config.get(configParameter);
            } else {
                value = configParameter.parse(strValue);
            }
            values.put(configParameter, value != null ? value : NULL);
        }
    }

    @Override
    public <T> T get(ConfigParameter<T> parameter) {
        //noinspection unchecked
        T res = (T) values.get(parameter);
        if (res == null) {
            throw new ConfigurationException("Unknown parameter: " + parameter);
        }
        return res == NULL ? null : res;
    }

    private Properties readProperties(String customizationName, ConfigImpl config) {
        Properties fileProperties = readFileProperties(customizationName, config);
        Properties systemProperties = CustomizationHelper.readSystemProperties(customizationName);

        Properties res = new Properties();
        res.putAll(fileProperties);
        res.putAll(systemProperties);

        return res;
    }


    private Properties readFileProperties(String name, Config config) {
        String root = CustomizationHelper.getCustomizationRoot(config);
        File file = new File(root, name + File.separator + "conf/foros-ui.properties");
        Properties properties = new Properties();
        if (file.exists()) {
            try(FileInputStream is = new FileInputStream(file)) {
                properties.load(is);
            } catch (IOException e) {
                throw new RuntimeException("Wrong customization: " + name, e);
            }
        }
        return properties;
    }

}
