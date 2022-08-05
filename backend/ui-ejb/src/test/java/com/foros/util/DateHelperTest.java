package com.foros.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import group.Unit;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DateHelperTest {
    @Test
    @Category(Unit.class)
    public void parseDateTime() throws Exception {
        DateHelper.parseDateTime("01/01/2009 12:34", TimeZone.getTimeZone("GMT"), Locale.UK);

        try {
            DateHelper.parseDateTime("01/01/2009 12:34w", TimeZone.getTimeZone("GMT"), Locale.UK);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        try {
            DateHelper.parseDateTime("01/01/2009 12:34 AM", TimeZone.getTimeZone("GMT"), Locale.UK);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        DateHelper.parseDateTime("01/01/2009 12:34 AM", TimeZone.getTimeZone("GMT"), Locale.US);

        try {
            DateHelper.parseDateTime("01/01/2009 12:34", TimeZone.getTimeZone("GMT"), Locale.US);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        DateHelper.parseDateTimeTimeZone("02/17/2009 09:00 PM US/Arizona", Locale.US);
        DateHelper.parseDateTimeTimeZone("23.02.2009 17:00 Etc/GMT+3", new Locale("ru", "RU"));
    }

    @Test
    @Category(Unit.class)
    public void parseTime() throws Exception {
        DateHelper.parseTime("12:34", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.UK);

        try {
            DateHelper.parseTime("12:34kj", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.UK);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        try {
            DateHelper.parseTime("12:34 AM", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.UK);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        DateHelper.parseTime("12:34 AM", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.US);

        try {
            DateHelper.parseTime("12:34", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.US);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }

        try {
            DateHelper.parseTime("12:34df", DateFormat.SHORT, TimeZone.getTimeZone("GMT"), Locale.US);
            fail("Parse error expected");
        } catch (ParseException ignored) {
        }
    }

    @Test
    @Category(Unit.class)
    public void testFormatAndParseDateTime() {
        testFormatAndParseDateTime(Locale.US);
        testFormatAndParseDateTime(Locale.CHINA);
        testFormatAndParseDateTime(Locale.KOREA);
        testFormatAndParseDateTime(Locale.JAPAN);
    }

    private void testFormatAndParseDateTime(Locale locale) {
        DateTimeZone gmt = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = LocalDateTime.fromDateFields(new Date()).withSecondOfMinute(0).withMillisOfSecond(0).toDateTime(gmt).toDate();
        String str = DateHelper.formatDateTime(date, DateFormat.SHORT, DateFormat.SHORT, TimeZone.getTimeZone("GMT"), locale);
        LocalDateTime localDateTime = DateHelper.parseLocalDateTime(str, locale);
        Date date2 = localDateTime.toDateTime(gmt).toDate();
        assertEquals(date, date2);
    }


    @Test
    @Category(Unit.class)
    public void fullMonthBack() {
        assertEquals(new LocalDate(2011, 12, 1), DateHelper.fullMonthBack(new LocalDate(2012, 1, 10)));

        assertEquals(new LocalDate(2012, 2, 1), DateHelper.fullMonthBack(new LocalDate(2012, 3, 29)));
        assertEquals(new LocalDate(2012, 2, 1), DateHelper.fullMonthBack(new LocalDate(2012, 3, 10)));
        assertEquals(new LocalDate(2012, 1, 1), DateHelper.fullMonthBack(new LocalDate(2012, 2, 29)));
        assertEquals(new LocalDate(2012, 1, 1), DateHelper.fullMonthBack(new LocalDate(2012, 2, 10)));

        assertEquals(new LocalDate(2013, 2, 1), DateHelper.fullMonthBack(new LocalDate(2013, 3, 29)));
        assertEquals(new LocalDate(2013, 2, 1), DateHelper.fullMonthBack(new LocalDate(2013, 3, 10)));
        assertEquals(new LocalDate(2013, 1, 1), DateHelper.fullMonthBack(new LocalDate(2013, 2, 28)));
        assertEquals(new LocalDate(2013, 1, 1), DateHelper.fullMonthBack(new LocalDate(2013, 2, 10)));
    }
}
