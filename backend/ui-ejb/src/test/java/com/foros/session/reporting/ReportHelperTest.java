package com.foros.session.reporting;

import group.Unit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;

@Category(Unit.class)
public class ReportHelperTest {
    @Test
    public void isLessThanMonth() {
        checkForTimeZone("Australia/Sydney");
        checkForTimeZone("Europe/London");
    }

    private void checkForTimeZone(String zoneId) {
        DateTimeZone timeZone = DateTimeZone.forID(zoneId);
        DateTime start = new DateTime(2010, 5, 1, 0, 0, 0, 0, timeZone);
        DateTime end = new DateTime(2010, 5, 31, 0, 0, 0, 0, timeZone);

        assertTrue(ReportHelper.isLessThanMonth(start, end));
    }
}
