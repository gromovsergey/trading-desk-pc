package com.foros.util;

import com.foros.model.currency.Currency;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.admin.CurrencyConverter;
import com.foros.test.CurrentUserRule;
import com.foros.web.taglib.NumberFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import group.Unit;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class NumberFormatterTest {
    @Rule
    public CurrentUserRule currentUserRule = new CurrentUserRule();

    @Test
    @Category(Unit.class)
    public void formatCurrencyValue() throws java.text.ParseException {
        CurrentUserSettingsHolder.setLocale(Locale.US);

        assertEquals("$5.55", NumberFormatter.formatCurrency("5.554", "USD"));
        assertEquals("$5.56", NumberFormatter.formatCurrency("5.555", "USD"));
        CurrentUserSettingsHolder.setLocale(new Locale("ru","RU"));
        assertEquals("5,56 \u0440\u0443\u0431.", NumberFormatter.formatCurrency("5,555", "RUB"));
        CurrentUserSettingsHolder.setLocale(new Locale("be","BY"));
        assertEquals("\u0420\u0443\u04316", NumberFormatter.formatCurrency("5,555", "BYR"));
        CurrentUserSettingsHolder.setLocale(new Locale("ar","TN"));
        assertEquals("\u062f.\u062a.\u200f 5.555", NumberFormatter.formatCurrency("5.555", "TND"));
        CurrentUserSettingsHolder.setLocale(new Locale("ko","KR"));
        assertEquals("\uffe66", NumberFormatter.formatCurrency("5.555", "KRW"));
        assertNull(NumberFormatter.formatCurrency(null, "KRW"));
    }

    @Test
    @Category(Unit.class)
    public void formatAndConvertIntoCurrency() throws java.text.ParseException {
        Currency rub = new Currency(1L, "RUB", 2);
        Currency byr = new Currency(2L, "BYR", 0);
        Currency krw = new Currency(3L, "KRW", 0);
        Currency gbp = new Currency(4L, "GBP", 2);

        HashMap<Long, BigDecimal> rates = new HashMap<Long, BigDecimal>();
        rates.put(rub.getId(), new BigDecimal("31.1545"));
        rates.put(byr.getId(), new BigDecimal("3015.52"));
        rates.put(krw.getId(), new BigDecimal("1223.92"));
        rates.put(gbp.getId(), new BigDecimal("0.66076"));

        CurrentUserSettingsHolder.setLocale(new Locale("ru", "RU"));
        assertEquals("172,91 \u0440\u0443\u0431.", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("5.55"), new CurrencyConverter(rub, rates)));
        assertEquals("3\u00A0115\u00A0450,00 \u0440\u0443\u0431.", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("100000"), new CurrencyConverter(rub, rates)));
        assertEquals("261,68 \u0440\u0443\u0431.", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("5.55"), gbp.getId(), new CurrencyConverter(rub, rates)));

        CurrentUserSettingsHolder.setLocale(new Locale("ko", "KR"));
        assertEquals("\uffe66,799", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("5.555"), new CurrencyConverter(krw, rates)));
        assertEquals("\uffe610,289", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("5.555"), gbp.getId(), new CurrencyConverter(krw, rates)));
        assertEquals("\uffe66", NumberFormatter.formatAndConvertIntoCurrency(new BigDecimal("5.555"), krw.getId(), new CurrencyConverter(krw, rates)));

        assertNull(NumberFormatter.formatAndConvertIntoCurrency(null, gbp.getId(), new CurrencyConverter(rub, rates)));
        assertNull(NumberFormatter.formatAndConvertIntoCurrency(null, new CurrencyConverter(rub, rates)));
    }

    @Test
    @Category(Unit.class)
    public void formatCurrencyValueExt() throws java.text.ParseException {
        CurrentUserSettingsHolder.setLocale(Locale.US);
        assertEquals("$5.554", NumberFormatter.formatCurrency("5.554", "USD", 3));
        assertEquals("$5.555", NumberFormatter.formatCurrency("5.555", "USD", 3));
        assertEquals("$10.2356", NumberFormatter.formatCurrency("10.23564", "USD", 4));
        assertEquals("$10.2357", NumberFormatter.formatCurrency("10.23565", "USD", 4));
        CurrentUserSettingsHolder.setLocale(new Locale("ru","RU"));
        assertEquals("5,555 \u0440\u0443\u0431.", NumberFormatter.formatCurrency("5,555", "RUB", 3));
        CurrentUserSettingsHolder.setLocale(new Locale("be","BY"));
        assertEquals("\u0420\u0443\u04315,56", NumberFormatter.formatCurrency("5,555", "BYR", 2));
    }

    @Test
    @Category(Unit.class)
    public void formatNumber() throws java.text.ParseException {
        CurrentUserSettingsHolder.setLocale(Locale.US);
        assertEquals("5.554", NumberFormatter.formatNumber("5.554", 3));
        assertEquals("5.555", NumberFormatter.formatNumber("5.555", 3));
        assertEquals("10.2356", NumberFormatter.formatNumber("10.23564", 4));
        CurrentUserSettingsHolder.setLocale(new Locale("ru", "RU"));
        assertEquals("5,555", NumberFormatter.formatNumber("5,555", 3));
        CurrentUserSettingsHolder.setLocale(new Locale("be", "BY"));
        assertEquals("6", NumberFormatter.formatNumber("5,555", 0));
    }
}
