package com.foros.session.reporting.parameters;

import group.Jaxb;
import group.Unit;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category({ Unit.class, Jaxb.class })
public class LocalDateXmlAdapterTest {
    @Test
    public void localDate() throws Exception {
        testMarshal("2011-02-16", "2011-02-16");
        testMarshal("2011-02-16", "2011-02-16Z");
        // time is ignored
        testMarshal("2011-02-16", "2011-02-16T22:34:45");
        testMarshal("2011-02-16", "2011-02-16+01:00");
        testMarshal("2011-02-16", "2011-02-16-01:00");
    }

    private void testMarshal(String expected, String toMarshal) throws Exception {
        LocalDateXmlAdapter adapter = new LocalDateXmlAdapter();
        assertEquals(new LocalDate(expected), adapter.unmarshal(toMarshal));
    }
}
