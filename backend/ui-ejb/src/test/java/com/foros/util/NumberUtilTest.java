package com.foros.util;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.test.CurrentUserRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import group.Unit;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class NumberUtilTest {

    @Rule
    public CurrentUserRule currentUserRule = new CurrentUserRule();

    @Test(expected = NumberFormatException.class)
    public void nullAsDouble() {
        NumberUtil.parseDouble(null, 0);
    }

    @Test(expected = NumberFormatException.class)
    public void emptyAsDouble() {
        NumberUtil.parseDouble("", 0);
    }

    @Test(expected = NumberFormatException.class)
    public void stringAsDouble() {
        NumberUtil.parseDouble("bug", 0);
    }

    @Test
    public void asDouble() {
        assertEquals(NumberUtil.parseDouble("0", 0), 0.0, 0.0);
        assertEquals(NumberUtil.parseDouble("0,0", 0), 0.0, 0.0);
        assertEquals(NumberUtil.parseDouble("0.1200", 2), 0.12, 0.0);
        assertEquals(NumberUtil.parseDouble("0.01200", 3), 0.012, 0.0);

        CurrentUserSettingsHolder.setLocale(new Locale("ko"));

        assertEquals(NumberUtil.parseDouble("0", 0), 0.0, 0.0);
        assertEquals(NumberUtil.parseDouble("0,0", 0), 0.0, 0.0);

        CurrentUserSettingsHolder.setLocale(new Locale("ru","GB"));
        assertEquals(NumberUtil.parseDouble("12,07500", 3), 12.075, 0.0);
    }

    @Test(expected = NumberFormatException.class)
    public void nullAsInt() {
        NumberUtil.parseInt(null);
    }

    @Test(expected = NumberFormatException.class)
    public void emptyAsInt() {
        NumberUtil.parseInt("");
    }

    @Test(expected = NumberFormatException.class)
    public void stringAsInt() {
        NumberUtil.parseInt("bug");
    }

    @Test
    public void asInt() {
        assertEquals(NumberUtil.parseInt("0"), 0);
        assertEquals(NumberUtil.parseInt("-1"), -1);
    }

    @Test(expected = NumberFormatException.class)
    public void stringWithDelimiterAsInt() {
        NumberUtil.parseInt("0.0");
    }

    @Test(expected = NumberFormatException.class)
    public void nullAsLong() {
        NumberUtil.parseLong(null);
    }

    @Test(expected = NumberFormatException.class)
    public void emptyAsLong() {
        NumberUtil.parseLong("");
    }

    @Test(expected = NumberFormatException.class)
    public void stringAsLong() {
        NumberUtil.parseLong("bug");
    }

    @Test
    public void asLong() {
        assertEquals(NumberUtil.parseLong("0"), 0);
        assertEquals(NumberUtil.parseLong("-1"), -1);
    }

    @Test(expected = NumberFormatException.class)
    public void stringWithDelimiterAsLong() {
        NumberUtil.parseLong("0.0");
    }

    @Test
    public void parseBigDecimaForUS() throws ParseException {
        DecimalFormat nf_en = (DecimalFormat) NumberFormat.getInstance(Locale.US);

        assertEquals(new BigDecimal("122122.44"), NumberUtil.parseBigDecimal("122,122.44", nf_en));
        assertEquals(new BigDecimal("122122"), NumberUtil.parseBigDecimal("122,122", nf_en));
        assertNull(NumberUtil.parseBigDecimal(null, nf_en));
        assertNull(NumberUtil.parseBigDecimal("", nf_en));
    }

    @Test(expected = ParseException.class)
    public void parseStringAsBigDecimaForUS() throws ParseException {
        DecimalFormat nf_en = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        NumberUtil.parseBigDecimal("122 122", nf_en);
    }

    @Test
    public void parseBigDecimalForRU() throws ParseException {
        DecimalFormat nf_ru = (DecimalFormat) NumberFormat.getInstance(new Locale("ru"));
        assertEquals(new BigDecimal("122122.44"), NumberUtil.parseBigDecimal("122122,44", nf_ru));
        assertEquals(new BigDecimal("122122"), NumberUtil.parseBigDecimal("122122", nf_ru));
        assertNull(NumberUtil.parseBigDecimal(null, nf_ru));
        assertNull(NumberUtil.parseBigDecimal("", nf_ru));
    }

    @Test(expected = ParseException.class)
    public void parseStringAsBigDecimalForRU() throws ParseException {
        DecimalFormat nf_ru = (DecimalFormat) NumberFormat.getInstance(new Locale("ru"));
        NumberUtil.parseBigDecimal("1,122,122", nf_ru);
    }

    @Test
    public void bigInteger() {
        assertTrue(NumberUtil.isBigInteger("999999999999999999999999999"));
        assertFalse(NumberUtil.isBigInteger("12.3456"));
        assertFalse(NumberUtil.isBigInteger("12,3456"));
        assertFalse(NumberUtil.isBigInteger("bad"));
    }

    @Test
    public void fraction() {
        assertEquals(new BigDecimal("10"), NumberUtil.addFraction(BigDecimal.ZERO, -1));
        assertEquals(new BigDecimal("1"), NumberUtil.addFraction(BigDecimal.ZERO, 0));
        assertEquals(new BigDecimal("0.1"), NumberUtil.addFraction(BigDecimal.ZERO, 1));
        assertEquals(new BigDecimal("0.01"), NumberUtil.addFraction(BigDecimal.ZERO, 2));

        assertEquals(new BigDecimal("0"), NumberUtil.subtractFraction(BigDecimal.TEN, -1));
        assertEquals(new BigDecimal("9"), NumberUtil.subtractFraction(BigDecimal.TEN, 0));
        assertEquals(new BigDecimal("9.9"), NumberUtil.subtractFraction(BigDecimal.TEN, 1));
        assertEquals(new BigDecimal("9.99"), NumberUtil.subtractFraction(BigDecimal.TEN, 2));
    }
}
