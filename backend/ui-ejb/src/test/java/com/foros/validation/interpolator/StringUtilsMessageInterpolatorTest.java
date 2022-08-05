package com.foros.validation.interpolator;

import com.foros.AbstractUnitTest;

import static org.junit.Assert.assertEquals;
import group.Resource;
import group.Unit;
import java.util.Locale;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class StringUtilsMessageInterpolatorTest extends AbstractUnitTest {
    private Object[] params = null;
    private StringUtilsMessageInterpolator interpolator = new StringUtilsMessageInterpolator(Locale.UK);

    @Test
    public void interpolate() {
        //same as input
        assertEquals(null, interpolate(null));
        assertEquals("2", interpolate("2"));
        assertEquals("234", interpolate("234"));
        assertEquals("not a resource", interpolate("not a resource"));

        //interpolated
        assertEquals("Field is required", interpolate("errors.field.required"));

        //interpolated with params
        params = new Object[] {"Test"};
        assertEquals("Test is required", interpolate("errors.required"));
    }

    private String interpolate(String messageTemplate) {
        return interpolator.interpolate(new MessageTemplate(messageTemplate, params));
    }
}
