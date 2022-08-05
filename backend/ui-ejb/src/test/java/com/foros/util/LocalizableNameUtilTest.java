package com.foros.util;

import static com.foros.util.StringUtilTest.TEST_KEY;
import static com.foros.util.StringUtilTest.TEST_VALUE;
import static com.foros.util.StringUtilTest.TEST_VALUE_RU;
import com.foros.model.LocalizableName;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.ServiceLocatorMock;
import com.foros.test.CurrentUserRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import group.Resource;
import group.Unit;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class LocalizableNameUtilTest {

    @Rule
    public CurrentUserRule currentUserRule = new CurrentUserRule();

    @Rule
    public ServiceLocatorMock serviceLocatorMock = ServiceLocatorMock.getInstance();

    @Test
    public void localizedValue() {
        Locale.setDefault(Locale.US);

        // Check with no locale
        assertEquals(null, getLocalizedValue(null, null));
        assertEquals("default", getLocalizedValue("default", null));
        assertEquals(TEST_VALUE, getLocalizedValue("default", TEST_KEY));

        CurrentUserSettingsHolder.setLocale(Locale.US);

        assertEquals(null, getLocalizedValue(null, null));
        assertEquals("default", getLocalizedValue("default", null));
        assertEquals(TEST_VALUE, getLocalizedValue("default", TEST_KEY));

        Locale russian = new Locale("ru");
        CurrentUserSettingsHolder.setLocale(russian);
        assertEquals(null, getLocalizedValue(null, null));
        assertEquals("default", getLocalizedValue("default", null));
        assertEquals(TEST_VALUE_RU, getLocalizedValue("default", TEST_KEY));

        assertEquals(TEST_VALUE, getLocalizedValueForLocale("default", TEST_KEY, Locale.US));
        assertEquals(TEST_VALUE_RU, getLocalizedValueForLocale("default", TEST_KEY, russian));
    }

    @Test
    public void localizedValueForLocale() {
        LocalizableName ln = new LocalizableName();

        // case: default name null
        ln.setDefaultName(null);
        ln.setResourceKey("invalid.key");

        assertNull(LocalizableNameUtil.getLocalizedValue(ln, true));
        assertTrue(LocalizableNameUtil.getLocalizedValue(ln, false).endsWith("invalid.key???"));

        // case: default name NOT null
        ln = new LocalizableName();
        ln.setDefaultName("default");
        ln.setResourceKey("invalid.key");

        assertEquals("default", LocalizableNameUtil.getLocalizedValue(ln, true));
        assertEquals("default", LocalizableNameUtil.getLocalizedValue(ln, false));
    }

    private String getLocalizedValue(String defaultName, String resourceKey) {
        LocalizableName ln = new LocalizableName(defaultName, resourceKey);
        return LocalizableNameUtil.getLocalizedValue(ln);
    }

    private String getLocalizedValueForLocale(String defaultName, String resourceKey, Locale locale) {
        LocalizableName ln = new LocalizableName(defaultName, resourceKey);
        return LocalizableNameUtil.getLocalizedValue(ln, locale);
    }
}
