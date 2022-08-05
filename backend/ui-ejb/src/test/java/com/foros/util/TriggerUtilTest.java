package com.foros.util;

import com.foros.model.channel.trigger.UrlTrigger;
import com.foros.util.url.TriggerQANormalization;
import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

@Category(Unit.class)
public class TriggerUtilTest extends Assert {

    @Test
    public void testNormalizeUrl() {
        checkURLNormalization("http://", "/");
        checkURLNormalization("http://confluence.ocslab.com/pages/editpage.action?pageId=458773", "confluence.ocslab.com/pages/editpage.action?pageid=458773");
        checkURLNormalization("http://confluence.ocslab.com", "confluence.ocslab.com/");
        checkURLNormalization("confluence.ocslab.com/", "confluence.ocslab.com/");
        checkURLNormalization("confluence.ocslab.com:80", "confluence.ocslab.com/");
        checkURLNormalization("confluence.ocslab.com/pages?any=true#3", "confluence.ocslab.com/pages?any=true");
        checkURLNormalization("confluence.ocslab.com/pages?any=true#3&p=1", "confluence.ocslab.com/pages?any=true");
        checkURLNormalization("http://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B5%D0%BA%D1%81%D0%B0%D0%BD%D0%B4%D1%80_%D0%9F%D1%83%D1%88%D0%BA%D0%B8%D0%BD", "ru.wikipedia.org/wiki/\u0410\u043b\u0435\u043a\u0441\u0430\u043d\u0434\u0440_\u041f\u0443\u0448\u043a\u0438\u043d");
        checkURLNormalization("http://ru.wikipedia.org/wiki/%D0%90%D0%BB%D0%B5%D0%BA%D1%81%D0%B0%D0%BD%D0%B4%D1%80%20%D0%9F%D1%83%D1%88%D0%BA%D0%B8%D0%BD", "ru.wikipedia.org/wiki/\u0410\u043b\u0435\u043a\u0441\u0430\u043d\u0434\u0440%20\u041f\u0443\u0448\u043a\u0438\u043d");
        checkURLNormalization("http://calculator.org/calculate?2%2b2%3d", "calculator.org/calculate?2%2b2%3d");
        checkURLNormalization("normalization.com/path?2+2=", "normalization.com/path?2+2=");
        checkURLNormalization("http://WwW.norMALIZation.com:80/PaTh?TeXt", "normalization.com/path?text");
        checkURLNormalization("http://WwW.norMALIZation.com:80", "normalization.com/");
        checkURLNormalization("http://www.site.ru/index.html?param=%1", "site.ru/index.html?param=%1");
        checkURLNormalization("http://www.site.ru/?p1=%1y3&p2=2", "site.ru/?p1=%1y3&p2=2");
        checkURLNormalization("http://www.site.ru/?p1=%%41", "site.ru/?p1=%a");
        checkURLNormalization("http://url.com/ABC DEF", "url.com/abc%20def");
        checkURLNormalization("http://url.com/ABC%20 DEF", "url.com/abc%20%20def");

        checkURLNormalization("ad4.yieldmanager.com/imp?z=160x600&s=2&http%3a%2f%2fwww%252Espreadfood%252Ecom",
                "ad4.yieldmanager.com/imp?z=160x600&s=2&http%3a%2f%2fwww.spreadfood.com");

        checkURLNormalization("http://www.ru", "www.ru/");
        checkURLNormalization("http://www.ru/index.html", "www.ru/index.html");
        checkURLNormalization("http://www.ru/index.html?id=1.1", "www.ru/index.html?id=1.1");
        checkURLNormalization("http://www.ru/index.html?id=1.%2e1", "www.ru/index.html?id=1..1");
        checkURLNormalization("http://www.ru/ABC%20 DEF", "www.ru/abc%20%20def");
        checkURLNormalization("http://www.www.site.ru/", "site.ru/");
        checkURLNormalization("http://www.www.www.site.ru/", "site.ru/");
        checkURLNormalization("www.ru/%2a", "www.ru/%2a");
        checkURLNormalization("search.360buy.com/search?keyword=%C2%ED%B6%A1%D1%A5", "search.360buy.com/search?keyword=%c2%ed%b6%a1%d1%a5");
        checkURLNormalization("http://www.ru:zzz@www.222.ru", "www.ru:zzz@222.ru/");
    }

    @Test
    public void testEncodeUrl() {
        String srcURL = "http://ya.ru:8080/?q=%20\u043f\u0440\u0438\u0432\u0435\u0442:&%20,;=:/?[]@!$&'()*+,;=";
        String encoded = StringUtil.encodeUrl(srcURL);
        String expectedResult = "http%3A%2F%2Fya.ru%3A8080%2F%3Fq%3D%2520%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82%3A%26%2520%2C%3B%3D%3A%2F%3F%5B%5D%40%21%24%26%27%28%29*%2B%2C%3B%3D";
        assertEquals(expectedResult, encoded);

        srcURL = "http://url.com |1|2|3";
        encoded = StringUtil.encodeUrl(srcURL);
        expectedResult = "http%3A%2F%2Furl.com+%7C1%7C2%7C3";
        assertEquals(expectedResult, encoded);
    }

    private void checkURLNormalization(String srcURL, String expectedResult) {
        String normalized = TriggerQANormalization.normalizeURL(srcURL);
        assertEquals(expectedResult, normalized);
    }

    @Test
    public void testUrlTriggerGroup() {
        UrlTrigger trigger = createUrlTrigger("\"www.domain.com/path?param\"", true);
        String group = trigger.getGroup();
        assertEquals("domain.com", group);
    }

    @Test
    public void testUrlTriggerMask() {
        String errors = "";
        errors += checkMasked("", "", true);
        errors += checkMasked("abc.ru", "abc.ru", true);
        errors += checkMasked("abc.ru", "abc.ru/xyz", false);
        errors += checkMasked("abc.ru/xyz", "abc.ru", true);
        errors += checkMasked("www.abc.ru", "abc.ru/xyz", false);
        errors += checkMasked("abc.ru/xyz", "www.abc.ru", true);
        errors += checkMasked("abc.ru", "www.abc.ru/xyz", false);
        errors += checkMasked("www.abc.ru/xyz", "abc.ru", true);
        errors += checkMasked("\"abc.ru/xyz\"", "abc.ru", true);
        errors += checkMasked("abc.ru/xyz", "\"abc.ru\"", false);
        assertTrue("UrlTriggerMask test failed:" + errors, errors.length() == 0);
    }

    private String checkMasked(String one, String two, boolean expectation) {
        boolean result1 = createUrlTrigger(one, false).maskedBy(createUrlTrigger(two, false));
        if (result1 != expectation) {
            return "\n[" + two + "]" + (result1 ? " " : " doesn't ") + "mask" + " [" + one + "]";
        }
        boolean result2 = createUrlTrigger(one, false).maskedBy(createUrlTrigger(two, true));
        if (result2 != expectation) {
            return "\nNegative [" + two + "]" + (result2 ? " " : " doesn't ") + "mask" + " [" + one + "]";
        }
        if(expectation) {
            boolean result3 = createUrlTrigger(one, true).maskedBy(createUrlTrigger(two, false));
            if (result3) {
                return "\n[" + two + "]" + (result3 ? " " : " doesn't ") + "mask" + " negative [" + one + "]";
            }
        }
        return "";
    }

    @Test
    public void testMasked() {
        UrlTrigger t1;
        UrlTrigger t2;

        // same positive & positive
        t1 = createUrlTrigger("d.ru", false);
        t2 = createUrlTrigger("d.ru", false);
        maskUrlTriggers(t1, t2);
        assertTrue(t1.isMasked() ^ t2.isMasked());

        // same negative & positive
        t1 = createUrlTrigger("d.ru", true);
        t2 = createUrlTrigger("d.ru", false);
        maskUrlTriggers(t1, t2);
        assertFalse(t1.isMasked());
        assertTrue(t2.isMasked());

        // same positive & negative
        t1 = createUrlTrigger("d.ru", false);
        t2 = createUrlTrigger("d.ru", true);
        maskUrlTriggers(t1, t2);
        assertTrue(t1.isMasked());
        assertFalse(t2.isMasked());

        // same negative & negative
        t1 = createUrlTrigger("d.ru", true);
        t2 = createUrlTrigger("d.ru", true);
        maskUrlTriggers(t1, t2);
        assertTrue(t1.isMasked() ^ t2.isMasked());

        // positive & positive
        t1 = createUrlTrigger("d.ru/path", false);
        t2 = createUrlTrigger("d.ru", false);
        maskUrlTriggers(t1, t2);
        assertTrue(t1.isMasked());
        assertFalse(t2.isMasked());
    }

    private void maskUrlTriggers(UrlTrigger t1, UrlTrigger t2) {
        List<UrlTrigger> list = new ArrayList<UrlTrigger>(2);
        list.add(t1);
        list.add(t2);
        UrlTrigger.calcMasked(list);
    }

    @Test
    public void testSplit() {
        assertEquals("size", 0, TriggerUtil.splitPhrase(null).size());
        assertEquals("size", 0, TriggerUtil.splitPhrase("").size());
        assertEquals("size", 0, TriggerUtil.splitPhrase("  ").size());
        assertEquals("size", 1, TriggerUtil.splitPhrase("a").size());
        assertEquals("size", 2, TriggerUtil.splitPhrase("a   and").size());
        assertEquals("size", 1, TriggerUtil.splitPhrase("\"a   and\"").size());
        List<String> splitPhrase = TriggerUtil.splitPhrase("\"a   and\" b");
        assertEquals("size", 2, splitPhrase.size());
        assertTrue("phrase", splitPhrase.contains("\"a   and\""));
        assertTrue("phrase", splitPhrase.contains("b"));
        assertTrue(TriggerUtil.splitPhrase("aaa\"bbb\"").contains("aaa"));
        assertTrue(TriggerUtil.splitPhrase("aaa\"bbb\"").contains("\"bbb\""));
        assertTrue(TriggerUtil.splitPhrase("\"aaa bbb").contains("aaa"));
        assertTrue(TriggerUtil.splitPhrase("\"aaa bbb").contains("bbb"));
        assertTrue(TriggerUtil.splitPhrase("aaa\" bbb").contains("aaa"));
        assertTrue(TriggerUtil.splitPhrase("aaa\" bbb").contains("bbb"));
        assertTrue(TriggerUtil.splitPhrase("aaa \"bbb").contains("aaa"));
        assertTrue(TriggerUtil.splitPhrase("aaa \"bbb").contains("bbb"));
        assertTrue(TriggerUtil.splitPhrase("aaa bbb\"").contains("aaa"));
        assertTrue(TriggerUtil.splitPhrase("aaa bbb\"").contains("bbb"));
    }

    private UrlTrigger createUrlTrigger(String original, boolean negative) {
        return new UrlTrigger(original, negative);
    }
}
