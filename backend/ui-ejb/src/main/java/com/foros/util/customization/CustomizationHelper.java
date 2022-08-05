package com.foros.util.customization;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletRequest;

public class CustomizationHelper {
    private static final String CUSTOMIZATION_PREFIX = "customization_";
    private static final String CUSTOMIZATION_PROP_PREFIX = "customizationprop_";
    private static final Set<String> customizationNames;
    private static ThreadLocal<String> customizationNameHolder = new ThreadLocal<String>();

    static {
        Set<String> names = new HashSet<>();
        Properties properties = System.getProperties();
        for (String name : properties.stringPropertyNames()) {
            if (name.startsWith(CUSTOMIZATION_PREFIX)) {
                names.add(properties.getProperty(name));
            }
        }
        customizationNames = Collections.unmodifiableSet(names);
    }

    static void setCustomizationName(ServletRequest request) {
        String key = CUSTOMIZATION_PREFIX + request.getServerName() + "_" + request.getLocalPort();
        String name = System.getProperty(key);
        customizationNameHolder.set(name);
    }

    static void clearCustomizationName() {
        customizationNameHolder.set(null);
    }

    public static String getCustomizationName() {
        return customizationNameHolder.get();
    }

    public static Set<String> getCustomizationNames() {
        return customizationNames;
    }

    public static String getCustomizationRoot(Config config) {
        return config.get(ConfigParameters.DATA_ROOT) + File.separator + config.get(ConfigParameters.CUSTOMIZATIONS_FOLDER);
    }

    public static Properties readSystemProperties(String customizationName) {
        Properties res = new Properties();
        String prefix = CUSTOMIZATION_PROP_PREFIX + customizationName + "_";
        for (String name : System.getProperties().stringPropertyNames()) {
            if (name.startsWith(prefix)) {
                String key = name.substring(prefix.length());
                res.setProperty(key, System.getProperty(name));
            }
        }
        return res;
    }

    public static void overrideCustomization(String customization, Runnable task) {
        String prevCustomization = customizationNameHolder.get();
        try {
            customizationNameHolder.set(customization);
            task.run();
        } finally {
            customizationNameHolder.set(prevCustomization);
        }
    }
}
