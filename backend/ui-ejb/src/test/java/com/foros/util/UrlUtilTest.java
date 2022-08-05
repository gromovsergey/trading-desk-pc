package com.foros.util;

import group.Unit;

import com.foros.config.Config;

import static com.foros.config.ConfigParameters.CREATIVES_PATH;
import static com.foros.config.ConfigParameters.DATA_URL;

import com.foros.config.MockConfigService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test for UrlUtils
 */
@Category(Unit.class)
public class UrlUtilTest extends Assert {
    private String creativesURL;
    private Config config;

    @org.junit.Before public void setUp() throws Exception {
        MockConfigService mockConfigService = new MockConfigService();

        mockConfigService.set(DATA_URL, "http://some.host");
        mockConfigService.set(CREATIVES_PATH, "creatives");
        config = mockConfigService.detach();

        creativesURL = config.get(DATA_URL) + "/" + config.get(CREATIVES_PATH);
    }

    @Test
    public void testFormatFileUrl() {
        assertEquals( creativesURL + "/null", UrlUtil.formatFileUrl(null, config));
        assertEquals( creativesURL + "/some/file.txt", UrlUtil.formatFileUrl("/some/file.txt", config));
        assertEquals( creativesURL + "/some/file.txt", UrlUtil.formatFileUrl("some/file.txt", config));
        assertEquals( creativesURL + "/some/file.txt", UrlUtil.formatFileUrl("\\some\\file.txt", config));
        assertEquals( creativesURL + "/some/file/.txt", UrlUtil.formatFileUrl("\\some/file/.txt/", config));
    }

    @Test
    public void testPassBackUrl() {
        assertTrue(UrlUtil.isSchemaUrl("http://com.ru"));
        assertTrue(UrlUtil.isSchemaUrl("HTTP://com.ru"));
        assertTrue(UrlUtil.isSchemaUrl("httpS://com.ru"));
        assertTrue(UrlUtil.isSchemaUrl("httpS://"));
        assertTrue(UrlUtil.isSchemaUrl("http://"));
        assertFalse(UrlUtil.isSchemaUrl("<img>"));
        assertFalse(UrlUtil.isSchemaUrl(""));
        assertFalse(UrlUtil.isSchemaUrl(null));
    }

    @Test
    public void testConcat() {
        assertEquals("http://ab/cd", UrlUtil.concat("http://ab", "cd"));
        assertEquals("http://ab/cd", UrlUtil.concat("http://ab/", "cd"));
        assertEquals("http://ab/cd", UrlUtil.concat("http://ab/", "/cd"));
        assertEquals("http://ab/cd", UrlUtil.concat("http://ab", "/cd"));
    }

    @Test
    public void testStripSchema() {
        assertEquals("ab.com/TEST", UrlUtil.stripSchema("http://ab.com/TEST"));
        assertEquals("ab.com/TEST", UrlUtil.stripSchema("HTTP://ab.com/TEST"));
        assertEquals("ab.com/TEST", UrlUtil.stripSchema("HTTPs://ab.com/TEST"));
        assertEquals("ab.com/TEST", UrlUtil.stripSchema("ab.com/TEST"));
        assertEquals("", UrlUtil.stripSchema(""));
        assertEquals(null, UrlUtil.stripSchema(null));
    }

    @Test
    public void testAppendSchema() {
        assertEquals("http://ab.com/TEST", UrlUtil.appendSchema(Schema.HTTP, "http://ab.com/TEST"));
        assertEquals("HTTP://ab.com/TEST", UrlUtil.appendSchema(Schema.HTTP, "HTTP://ab.com/TEST"));
        assertEquals("HTTPs://ab.com/TEST", UrlUtil.appendSchema(Schema.HTTP, "HTTPs://ab.com/TEST"));
        assertEquals("http://ab.com/TEST", UrlUtil.appendSchema(Schema.HTTP, "ab.com/TEST"));
        assertEquals("", UrlUtil.appendSchema(Schema.HTTP, ""));
        assertEquals(null, UrlUtil.appendSchema(Schema.HTTP, null));
    }

    @Test
    public void testReplaceParamValue() throws Exception {
        StringBuilder url = new StringBuilder("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;prck=0&amp;testrequest=1&amp;glbfcap=0&amp;uid=PPPPPPPPPPPPPPPPPPPPPP%7C%7C");
        assertEquals("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;prck=0&amp;testrequest=1&amp;glbfcap=0&amp;uid=dummyValue", UrlUtil.replaceUidParamValue(url.toString(), "dummyValue"));

        url = new StringBuilder("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;uid=PPPPPPPPPPPPPPPPPPPPPP%7C%7C&amp;prck=0&amp;testrequest=1&amp;glbfcap=0");
        assertEquals("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;uid=dummyValue&amp;prck=0&amp;testrequest=1&amp;glbfcap=0", UrlUtil.replaceUidParamValue(url.toString(), "dummyValue"));

        url = new StringBuilder("http://a.foros-rubytest.net/nslookup");
        assertEquals("http://a.foros-rubytest.net/nslookup?uid=dummyValue", UrlUtil.replaceUidParamValue(url.toString(), "dummyValue"));

        url = new StringBuilder("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;prck=0&amp;testrequest=1&amp;glbfcap=0");
        assertEquals("http://a.foros-rubytest.net/services/nslookup?require-debug-info=body&amp;prck=0&amp;testrequest=1&amp;glbfcap=0&amp;uid=dummyValue", UrlUtil.replaceUidParamValue(url.toString(), "dummyValue"));

    }

}
