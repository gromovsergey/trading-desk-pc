package com.foros.config;

import java.util.Properties;

import group.Unit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

@Category(Unit.class)
public class ConfigImplTest {
    private final Properties properties = System.getProperties();

    @Before
    public void setUp() throws Exception {
        System.setProperties(new Properties());
    }

    @After
    public void tearDown() throws Exception {
        System.setProperties(properties);
    }

    @Test
    public void required() {
        try {
            new ConfigImpl(TestConfig.class);
            fail();
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("required"));
        }

        System.setProperty("str2", "val2");
        Config config = new ConfigImpl(TestConfig.class);
        assertNotNull(config);
    }

    @Test
    public void get() {
        // test defaults
        Config config = new ConfigImpl(TestConfig2.class);

        assertEquals(null, config.get(TestConfig2.STRING));
        assertEquals("def", config.get(TestConfig2.STRING_DEF));

        assertEquals(null, config.get(TestConfig2.INTEGER));
        assertEquals((Integer)11, config.get(TestConfig2.INTEGER_DEF));

        assertEquals(null, config.get(TestConfig2.LONG));
        assertEquals((Long)111L, config.get(TestConfig2.LONG_DEF));
    }

    @Test
    public void override() {
        // test overrides
        System.setProperty("str1", "val1");
        System.setProperty("str2", "val2");
        System.setProperty("int1", "22");
        System.setProperty("int2", "33");
        System.setProperty("long1", "222");
        System.setProperty("long2", "333");

        Config config = new ConfigImpl(TestConfig2.class);

        assertEquals("val1", config.get(TestConfig2.STRING));
        assertEquals("val2", config.get(TestConfig2.STRING_DEF));

        assertEquals((Integer)22, config.get(TestConfig2.INTEGER));
        assertEquals((Integer)33, config.get(TestConfig2.INTEGER_DEF));

        assertEquals((Long)222L, config.get(TestConfig2.LONG));
        assertEquals((Long)333L, config.get(TestConfig2.LONG_DEF));
    }

    private static class TestConfig {
        public static final ConfigParameter<String> STRING = new StringConfigParameter("str1", null);
        public static final ConfigParameter<String> STRING_REQUIRED = new StringConfigParameter("str2");
    }

    private static class TestConfig2 extends TestConfig {
        public static final ConfigParameter<String> STRING = new StringConfigParameter("str1", null);
        public static final ConfigParameter<String> STRING_DEF = new StringConfigParameter("str2", "def");

        public static final ConfigParameter<Integer> INTEGER = new IntegerConfigParameter("int1", null);
        public static final ConfigParameter<Integer> INTEGER_DEF = new IntegerConfigParameter("int2", 11);

        public static final ConfigParameter<Long> LONG = new LongConfigParameter("long1", null);
        public static final ConfigParameter<Long> LONG_DEF = new LongConfigParameter("long2", 111L);
    }
}
