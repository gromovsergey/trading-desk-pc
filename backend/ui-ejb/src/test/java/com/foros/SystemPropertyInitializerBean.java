package com.foros;

import java.util.Properties;

public class SystemPropertyInitializerBean {
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
        for (Object o : properties.keySet()) {
            String key = (String) o;
            System.setProperty(key, properties.getProperty(key));
        }
    }

    public void setClearProperties(Properties properties) {
        for (Object o : properties.keySet()) {
            String key = (String) o;
            System.clearProperty(key);
            this.properties.remove(key);
        }
    }
}
