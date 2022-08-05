package com.foros.util;

import group.Unit;

import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sun.enterprise.config.serverbeans.Resource;

/**
 * @author oleg_roshka
 */
@Category({ Unit.class, Resource.class })
public class PropertyHelperTest extends Assert {

    /**
     * Test of readProperties method, of class com.foros.util.PropertyHelper.
     */
    @Test
    public void testReadProperties() {
        String resource = "com/foros/util/prop_helper_test.properties";

        Properties properties = PropertyHelper.readProperties(resource);

        assertNotNull(properties);
        assertNotNull(properties.getProperty("test"));
        assertEquals("passed", properties.getProperty("test"));
    }

    @Test
    public void testSearch() {
        String resource = "com/foros/util/prop_helper_test.properties";

        Properties properties = PropertyHelper.readProperties(resource);

        Properties searchedProps = PropertyHelper.search("test.", properties);

        assertNotNull(searchedProps);
        assertNotNull(properties.getProperty("test.for.search.1"));
        assertEquals("2", properties.getProperty("test.for.search.2"));
    }
}
