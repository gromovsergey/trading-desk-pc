package com.foros.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author oleg_roshka
 */
public final class PropertyHelper {
    private PropertyHelper() {
    }

    public static Properties readProperties(String resource) {
        Properties properties = new Properties();

        InputStream resourceConf = getStream(resource);
        if (resourceConf == null) {
            throw new RuntimeException(resource + " not found");
        }

        try {
            properties.load(resourceConf);

            for (Enumeration<?> enumeration = properties.propertyNames(); enumeration.hasMoreElements();) {
                String key = (String)enumeration.nextElement();
                String value = System.getProperty(key);
                if (value != null) {
                    properties.setProperty(key, value);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource " + resource, e);
        }

        return properties;
    }

    private static InputStream getStream(String resource) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }

    public static Properties search(String nameRegxp, Properties props) {
        Properties result = new Properties();
        for (Enumeration<?> enumeration = props.propertyNames(); enumeration.hasMoreElements();) {
            String key = (String)enumeration.nextElement();
            if (key.matches(nameRegxp)) {
                result.setProperty(key, props.getProperty(key));
            }
        }
        
        return result;
    }
}
