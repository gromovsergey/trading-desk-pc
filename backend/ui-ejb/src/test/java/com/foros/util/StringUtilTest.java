package com.foros.util;

import com.foros.AbstractUnitTest;
import com.foros.security.SecurityContextMock;
import com.foros.security.currentuser.CurrentUserSettingsHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import group.Resource;
import group.Unit;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class StringUtilTest extends AbstractUnitTest {
    public static final String TEST_VALUE_RU = "\u0414\u0430";
    public static final String TEST_VALUE = "Yes";
    public static final String TEST_KEY = "yes";
    public static final String TEST_KEY_ARGS = "evicted.class.id";

    @Test
    public void isPropertyEmpty() {
        // test for a String argument
        assertTrue(StringUtil.isPropertyEmpty((String)null));
        assertTrue(StringUtil.isPropertyEmpty(""));
        assertTrue(StringUtil.isPropertyEmpty("\u0000\t\r\n "));
        assertFalse(StringUtil.isPropertyEmpty("\u0000\t\r\n test"));

        // test for a Character argument
        assertTrue(StringUtil.isPropertyEmpty((Character)null));
        assertTrue(StringUtil.isPropertyEmpty('\t'));
        assertTrue(StringUtil.isPropertyEmpty('\r'));
        assertTrue(StringUtil.isPropertyEmpty('\n'));
        assertTrue(StringUtil.isPropertyEmpty(' '));
        assertTrue(StringUtil.isPropertyEmpty('\u0000'));
        assertFalse(StringUtil.isPropertyEmpty('t'));
    }

    @Test
    public void formatBigDecimal() {
        currentUserRule.setLocale(Locale.US);
        assertEquals(StringUtil.formatBigDecimal(null, 0, true), null);
        assertEquals(StringUtil.formatBigDecimal(new BigDecimal("1.0"), 0, true), "1");
        assertEquals(StringUtil.formatBigDecimal(new BigDecimal("1.5"), 0, true), "2");
        assertEquals(StringUtil.formatBigDecimal(new BigDecimal("-1.5"), 0, true), "-2");
        assertEquals(StringUtil.formatBigDecimal(new BigDecimal("1.5"), 0, false), "2");
        assertEquals(StringUtil.formatBigDecimal(new BigDecimal("-1.5"), 0, false), "-2");
    }



    @Test
    public void removeZipSuffix() {
        assertEquals(StringUtil.removeZipSuffix(null), null);
        assertEquals(StringUtil.removeZipSuffix(""), "");
        assertEquals(StringUtil.removeZipSuffix("file.zip"), "file");
        assertEquals(StringUtil.removeZipSuffix("test.file.zip"), "test.file");
    }

    @Test
    public void trimFileName() {
        assertEquals(StringUtil.trimFileName(null), null);
        assertEquals(StringUtil.trimFileName(""), "");
        assertEquals(StringUtil.trimFileName("hello"), "hello");
        assertEquals(StringUtil.trimFileName("\\hello"), "hello");
        assertEquals(StringUtil.trimFileName("\\hello\\world"), "world");
        assertEquals(StringUtil.trimFileName("%2fhello\\world"), "world");
        assertEquals(StringUtil.trimFileName("%\\hello%5cworld"), "world");
    }

    @Test
    public void getLocalizedString() {
        currentUserRule.setLocale(Locale.US);
        assertEquals("???nullnull???", StringUtil.getLocalizedString(null, (Locale) null));
        assertEquals("???null" + TEST_KEY + "???", StringUtil.getLocalizedString(TEST_KEY, (Locale) null));
        assertEquals("???en_USnull???", StringUtil.getLocalizedString(null, Locale.US));
        assertEquals("???en_US???", StringUtil.getLocalizedString("", Locale.US));
        assertEquals("???en_USinvalid.key???", StringUtil.getLocalizedString("invalid.key", Locale.US));
        assertNull(StringUtil.getLocalizedString("invalid.key", Locale.US, true));
        assertEquals("???en_USinvalid.key???", StringUtil.getLocalizedString("invalid.key", Locale.US, false));
        assertEquals(TEST_VALUE, StringUtil.getLocalizedString(TEST_KEY, Locale.US));
        assertEquals(TEST_VALUE_RU, StringUtil.getLocalizedString(TEST_KEY, new Locale("ru", "RU")));
        assertEquals("Successfully evicted object : id=SecondArg class=FirstArg", StringUtil.getLocalizedString(TEST_KEY_ARGS, Locale.US, new String[]{"FirstArg", "SecondArg"}));
        assertNull(StringUtil.getLocalizedString("invalid.key", Locale.US, true, new String[]{"FirstArg", "SecondArg"}));
        assertEquals("???en_USinvalid.key???", StringUtil.getLocalizedString("invalid.key", Locale.US, false, new String[]{"FirstArg", "SecondArg"}));
        assertEquals("Successfully evicted object : id=SecondArg class=FirstArg", StringUtil.getLocalizedString(TEST_KEY_ARGS, Locale.US, new String[]{"FirstArg", "SecondArg"}));
        assertEquals(TEST_VALUE, StringUtil.getLocalizedString(TEST_KEY, Locale.US, new String[]{"FirstArg", "SecondArg"}));
        assertEquals(TEST_VALUE, StringUtil.getLocalizedString(TEST_KEY));
        String testDefault = "test";
        assertNull(StringUtil.getLocalizedStringWithDefault(null, null, Locale.US));
        assertEquals(testDefault, StringUtil.getLocalizedStringWithDefault(null, testDefault, Locale.US));
        assertEquals(testDefault, StringUtil.getLocalizedStringWithDefault("invalid.key", testDefault, Locale.US));
        assertEquals(TEST_VALUE, StringUtil.getLocalizedStringWithDefault(TEST_KEY, testDefault, Locale.US));
        SecurityContextMock.getInstance().tearDown();
    }

    @Test
    public void getLocalizedBigDecimal() {
        currentUserRule.setLocale(Locale.US);
        assertEquals("12,345.67", StringUtil.getLocalizedBigDecimal(new BigDecimal("12345.67")));
        assertEquals("12,345.7", StringUtil.getLocalizedBigDecimal(new BigDecimal("12345.67"), 1));
        assertEquals("100.0", StringUtil.getLocalizedBigDecimal(new BigDecimal("100"), 1));
        assertEquals("12,346", StringUtil.getLocalizedBigDecimal(new BigDecimal("12345.67"), 0));
        assertEquals("12,345.67891", StringUtil.getLocalizedBigDecimal(new BigDecimal("12345.67891"), -1));

        CurrentUserSettingsHolder.setLocale(new Locale("pt", "PT"));
        assertEquals("12.345,67", StringUtil.getLocalizedBigDecimal(new BigDecimal("12345.67")));
        assertEquals("100,0", StringUtil.getLocalizedBigDecimal(new BigDecimal("100"), 1));

        SecurityContextMock.getInstance().tearDown();
    }

    @Test
    public void formatMessage() {
        Locale ru = new Locale("ru", "RU");
        Locale uk = Locale.UK;
        assertEquals("{0}", StringUtil.formatMessage("{0}", ru));
        assertEquals("12\u00A0345", StringUtil.formatMessage("{0}", ru, 12345));
        assertEquals("12,345", StringUtil.formatMessage("{0}", uk, 12345));
        assertEquals("Hi Agent 7!", StringUtil.formatMessage("Hi {0} {1}!", ru, "Agent", 7));
    }

    @Test
    public void replaceRegexp() {
        assertEquals("best", StringUtil.replaceRegexp("test", Pattern.compile("^t"), "b"));
        assertEquals("tesb", StringUtil.replaceRegexp("test", Pattern.compile("t$"), "b"));
    }

    @Test
    public void getNumberInputMask() {
        Locale en = Locale.ENGLISH;
        assertEquals("##.#", StringUtil.getNumberInputMask(en, 3, 1));
        assertEquals("###", StringUtil.getNumberInputMask(en, 3, 0));
        assertEquals("#.###", StringUtil.getNumberInputMask(en, 3, 3));

        Locale ru = new Locale("ru");
        assertEquals("##,#", StringUtil.getNumberInputMask(ru, 3, 1));
        assertEquals("###", StringUtil.getNumberInputMask(ru, 3, 0));
        assertEquals("#,###", StringUtil.getNumberInputMask(ru, 3, 3));

    }

    @Test
    public void trimProperty() {
        assertEquals(null, StringUtil.trimProperty(null));
        assertEquals("", StringUtil.trimProperty("\u00a0 \u2007\u202F\t\r"));
        assertEquals("best\n\ttrim", StringUtil.trimProperty("\u00a0 best\n\ttrim \u00a0"));
        assertEquals("best\u00a0 \u00a0trim", StringUtil.trimProperty("\u00a0 best\u00a0 \u00a0trim \u00a0        "));
    }

    @Test
    public void trimLines() {
        assertEquals("text", StringUtil.trimLines("text"));
        assertEquals("text\r\ntext", StringUtil.trimLines("text\r\ntext"));
        assertEquals("text\r\ntext", StringUtil.trimLines("text\r\ntext\r\n"));
    }

    @Test
    public void trimLinesAndJoin() {
        String[] triggers1 = {"test  ", "11\r\n", "", "\r\n", "\n", "aaa"};
        String[] triggers2 = {"test  ", "aaa"};
        String[] triggers3 = {"test  ", "aaa", ""};
        assertEquals("test\r\n11\r\naaa", StringUtil.trimLinesAndJoin(Arrays.asList(triggers1)));
        assertEquals("test\r\naaa", StringUtil.trimLinesAndJoin(Arrays.asList(triggers2)));
        assertEquals("test\r\naaa", StringUtil.trimLinesAndJoin(Arrays.asList(triggers3)));
    }

    @Test
    public void splitByComma() {
        assertEquals(0, StringUtil.splitByComma(null).length);
        assertEquals(0, StringUtil.splitByComma("           ").length);
        assertEquals("234", StringUtil.splitByComma("234,666\n777")[0]);
        assertEquals("666", StringUtil.splitByComma("234,666\n777")[1]);
        assertEquals("777", StringUtil.splitByComma("234,666\n777")[2]);

        assertEquals("66", StringUtil.splitByComma("234,666\n777,,\n66")[5]);
    }

    @Test
    public void splitByLines() {
        assertEquals(0, StringUtil.splitByLines(null).length);
        assertEquals(0, StringUtil.splitByLines("           ").length);
        assertEquals("234,666", StringUtil.splitByLines("234,666\n777")[0]);
        assertEquals("777", StringUtil.splitByLines("234,666\n777")[1]);

        assertEquals("66", StringUtil.splitByLines("234,666\r\n777,,\n66")[2]);
        assertEquals("66", StringUtil.splitByLines("234,666\r\n777,,\n     \n66")[3]);
        assertEquals("66", StringUtil.splitByLines("234,666\r\n777,,\n\n\r\n66")[4]);
    }

    @Test
    public void splitAndTrim() {
        assertEquals(0, StringUtil.splitAndTrim(null).length);
        assertEquals(0, StringUtil.splitAndTrim("           ").length);
        assertEquals("234,666", StringUtil.splitAndTrim("234,666\n777")[0]);
        assertEquals("777", StringUtil.splitAndTrim("234,666\n777")[1]);

        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n66")[2]);
        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n\n66")[2]);
        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n\n\r\n66")[2]);

        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n     66")[2]);
        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n\n66       ")[2]);
        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n    \n66       ")[2]);
        assertEquals("66", StringUtil.splitAndTrim("234,666\n777,,\n\n\r\n    66   ")[2]);
    }

    @Test
    public void addComma() {
        assertEquals("", StringUtil.addComma(false, ""));
        assertEquals(", abc", StringUtil.addComma(true, "abc"));
        assertEquals("abc", StringUtil.addComma(false, "abc"));
    }

    @Test
    public void unquote() {
        assertEquals("\"", StringUtil.unquote("\"", false));
        assertEquals("", StringUtil.unquote("\"\"", false));
    }
}
