package com.foros.cache.jbc;

import org.hibernate.cache.jbc2.JBossCacheRegionFactory;

import java.util.Properties;

/**
 * This class is a replacement of a hibernate version of region factory.
 */
public class ForosCacheRegionFactory extends JBossCacheRegionFactory {
    public ForosCacheRegionFactory(Properties props) {
        this();
    }

    /**
     * Create a new MultiplexedJBossCacheRegionFactory.
     */
    public ForosCacheRegionFactory() {
        super(new ForosCacheInstanceManager());
    }
}
