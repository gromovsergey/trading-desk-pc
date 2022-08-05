package com.foros.util.url;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class TriggerURLValidatorTest {
    @Test
    public void validTriggerUrls() {
        assertValid("http://sun.com");
        assertValid("http://www.myasorubka.ru/");
        assertValid("http://sun.com/%D1%82%D0%B5%D1%81%D1%82");
        assertValid("Http://sun.com/calendar");
        assertValid("http://sun.com/fix.jsp?a=2-00&b=100");
        assertValid("HTTP://WWW.MYASORUBKA.RU/");
        assertValid("sun.com");
        assertValid("www.myasorubka.ru/");
        assertValid("sun.com/%D1%82%D0%B5%D1%81%D1%82");
        assertValid("sun.com/calendar");
        assertValid("sun.com/fix.jsp?a=2-00&b=100");
        assertValid("WWW.MYASORUBKA.RU/");
        assertValid("http://\u0645\u062b\u0627\u0644.\u0625\u062e\u062a\u0628\u0627\u0631"); // Arabic
        assertValid("http://\u4f8b\u5b50.\u6d4b\u8bd5"); // Chinese (simplified)
        assertValid("http://\u4f8b\u5b50.\u6e2c\u8a66"); // Chinese (traditional)
        assertValid("http://\u03c0\u03b1\u03c1\u03ac\u03b4\u03b5\u03b9\u03b3\u03bc\u03b1.\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae");// greek
        assertValid("http://\u0909\u0926\u093e\u0939\u0930\u0923.\u092a\u0930\u0940\u0915\u094d\u0937\u093e"); // Hindi
        assertValid("http://\u4f8b\u3048.\u30c6\u30b9\u30c8"); // Japanese
        assertValid("http://\uc2e4\ub840.\ud14c\uc2a4\ud2b8"); // Korean
        assertValid("http://\u0645\u062b\u0627\u0644.\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc"); // Persian
        assertValid("http://\u043f\u0440\u0438\u043c\u0435\u0440.\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435"); //Russian
        assertValid("http://\u043f\u0440\u0430\u0432\u0438\u0442\u0435\u043b\u044c\u0441\u0442\u0432\u043e.\u0440\u0444"); //Russian

        StringBuffer longUrl = new StringBuffer("www.com/");
        for (int i = 0; i < 2040; i++) {
            longUrl.append('a');
        }
        assertValid(longUrl.toString());
        assertValid("python.org//testers");
        assertValid("bracket.com/test(url)");

        // invalid domains are valid by rfc3986.
        assertValid("http://www.#space.com");
        assertValid("http://%D1%82%D0%B5%D1%81%D1%82.com/");

        assertValid("www.#space.com");
        assertValid("%D1%82%D0%B5%D1%81%D1%82.com/");
    }

    @Test
    public void invalidTriggerUrls() {
        assertInvalid("https://sun.com");
        assertInvalid("https://");
        assertInvalid("ftp://sun.com");
        assertInvalid("ftp://sun.com?");
        assertInvalid(null);
        assertInvalid("http://");
        assertInvalid("");
        assertInvalid("\"");

        assertInvalid("http:// www.space.com");
        assertInvalid("http://www. space.com");
        assertInvalid("http://www.\"space\".com");
        assertInvalid("http://www.%space.com");
        assertInvalid("http://www.<space>.com");

        assertInvalid(" www.space.com");
        assertInvalid("www. space.com");
        assertInvalid("www.\"space\".com");
        assertInvalid("www.%space.com");
        assertInvalid("www.<space>.com");
    }

    private void assertValid(String url) {
        assertTrue("Must be valid " + url, TriggerURLValidator.isValid(url));
    }

    private void assertInvalid(String url) {
        assertFalse("Must be invalid " + url, TriggerURLValidator.isValid(url));
    }
}