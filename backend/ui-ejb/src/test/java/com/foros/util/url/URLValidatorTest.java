package com.foros.util.url;

import com.foros.validation.code.BusinessErrors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category( Unit.class )
public class URLValidatorTest {
    TestUrlErrorHandler handler = new TestUrlErrorHandler();

    @Test
    public void validUrls() throws Exception {
        validUrl("http://localdomain");
        validUrl("http://sun.com");
        validUrl("https://sun.com");
        validUrl("http://www.myasorubka.ru/");
        validUrl("HTTP://WWW.MYASORUBKA.RU/");
        validUrl("Http://sun.com/calendar");
        validUrl("http://sun.com/%D1%82%D0%B5%D1%81%D1%82");
        validUrl("http://sun.com/fix.jsp?a=2-00&b=100");
        validUrl("http://bal.ru/bla//bla!$&'*+,;=");
        validUrl("http://bal.ru/(bla)");
        validUrl("http://bal.ru/(bla;");
        validUrl("http://nx_zero.blogger.com.br");
        validUrl("http://test.com:1234");
        validUrl("http://test.com///???###");
        validUrl("http://test.com/?#/?#/?#");
        validUrl("http://test.com#/?");
        validUrl("http://test.com?#/");
        validUrl("http://test.com:1234/qwe/rty/?zxc=123#asd");
        validUrl("http://\u0441\u0442\u043e.\u0440\u0444:1234");
        validUrl("http://test.\u0440\u0444/qwe/\u043f\u0440\u043e/?asd&\u0442\u043e\u043a&\u0442\u043e\u043a=\u0442\u043e\u043a#123");
        validUrl("http://www.\u043f\u0440\u043e\u0441\u0442\u043e\u043a\u0432\u0430\u0448\u0430.\u0440\u0444:8181"); // russian prostokvasha.rf
        validUrl("http://iba\u00f1ez.com");  //brazil ibanez
        validUrl("http://\u0645\u062b\u0627\u0644.\u0625\u062e\u062a\u0628\u0627\u0631"); // Arabic
        validUrl("http://\u4f8b\u3048.\u30c6\u30b9\u30c8"); // Japanese
        validUrl("http://\uc2e4\ub840.\ud14c\uc2a4\ud2b8"); // Korean
        validUrl("http://\u4f8b\u5b50.\u6d4b\u8bd5"); // Chinese (simplified)
        validUrl("http://\u4f8b\u5b50.\u6e2c\u8a66"); // Chinese (traditional)
        validUrl("http://\u03c0\u03b1\u03c1\u03ac\u03b4\u03b5\u03b9\u03b3\u03bc\u03b1.\u03b4\u03bf\u03ba\u03b9\u03bc\u03ae");// greek
        validUrl("http://\u0909\u0926\u093e\u0939\u0930\u0923.\u092a\u0930\u0940\u0915\u094d\u0937\u093e"); // Hindi
        validUrl("http://\u0645\u062b\u0627\u0644.\u0622\u0632\u0645\u0627\u06cc\u0634\u06cc"); // Persian
        validUrl("http://\u043f\u0440\u0438\u043c\u0435\u0440.\u0438\u0441\u043f\u044b\u0442\u0430\u043d\u0438\u0435"); //Russian
        validUrl("http://\u043f\u0440\u0430\u0432\u0438\u0442\u0435\u043b\u044c\u0441\u0442\u0432\u043e.\u0440\u0444"); //Russian
        validUrl("http://\u043F\u0440\u0430\u0432\u0438\u0442\u0435\u043B\u044C\u0441\u0442\u0432\u043E.\u0440\u0444/?search=\u043F\u0435\u0440\u0435\u0438\u043C\u0435\u043D\u0443\u0435\u043C+\u043C\u0438\u043B\u0438\u0446\u0438\u044E+\u0438+\u043C\u0435\u0434\u0438\u043A\u043E\u0432"); //Russian

        // do not conform to RFC1035
        validUrl("http://te$st.ru");
        validUrl("https://www.#space.com");
        validUrl("https://www.space.com./test");
        validUrl("http://%D1%82%D0%B5%D1%81%D1%82.com/");
        validUrl("http://www.xn--e1afmkfd.xn--80akhbykn3454f");
        validUrl("http://www.xn--e1afmkfd.xn--");
        validUrl("http://user@test.com");
        validUrl("http://user:@test.com");
        validUrl("http://:info@test.com");
        validUrl("http://user:info@test.com");
        validUrl("http://!/test");
    }

    @Test
    public void invalidUrls() throws Exception {
        invalidUrl("", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("http://", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https://", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("sun.com", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("ftp://sun.com", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("ftp://sun.com?", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("http:/test.com", BusinessErrors.URL_INVALID);
        invalidUrl("http:///test.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https:// www.space.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https://www. space.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https://www.\"space\".com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https://www.%space.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("https://www.<space>.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("http://test.com:abc", BusinessErrors.URL_INVALID_PORT);
        invalidUrl("http://test.com:1_2", BusinessErrors.URL_INVALID_PORT);
        invalidUrl("http://test.com:12:34", BusinessErrors.URL_INVALID_PORT);
        invalidUrl("http:/\u0441\u0442\u043e.\u0440\u0444:12:34", BusinessErrors.URL_INVALID);
        invalidUrl("ht_tp://nxzero.blogger.com.br", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("http:_//nxzero.blogger.com.br", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("http:/_/nxzero.blogger.com.br", BusinessErrors.URL_INVALID);
        invalidUrl("\u0445\u0442\u0442\u043F://\u043F\u0440\u0430\u0432\u0438\u0442\u0435\u043B\u044C\u0441\u0442\u0432\u043E.\u0440\u0444", BusinessErrors.URL_INVALID_SCHEMA);
        invalidUrl("http://www.test{$test}.ru", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("http://<>@test.com", BusinessErrors.URL_INVALID_USERINFO);
        invalidUrl("http:///test.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("http:///a=a", BusinessErrors.URL_INVALID_HOST);
        invalidUrl("//test.com", BusinessErrors.URL_INVALID_HOST);
    }

    @Test
    public void validUrlsNoSchema() throws Exception {
        validUrlNoSchema("google.com");
        validUrlNoSchema("http://google.com");

        validUrlNoSchema("google.com?://");
        validUrlNoSchema("http://google.com?://");

        validUrlNoSchema("google.com/://");
        validUrlNoSchema("http://google.com/://");

        validUrlNoSchema("user:info@google.com:80/://");
        validUrlNoSchema("http://user:info@google.com:80/://");

        validUrlNoSchema("google.com");
        validUrlNoSchema("google.com:80");
        validUrlNoSchema("google.com:80/");

        validUrlNoSchema("http/");
        validUrlNoSchema("http:80");
        validUrlNoSchema("http:80/");
    }

    @Test
    public void invalidUrlsNoSchema() throws Exception {
        invalidUrlNoSchema("goo<gle.com", BusinessErrors.URL_INVALID_HOST);
        invalidUrlNoSchema("http://goo<gle.com", BusinessErrors.URL_INVALID_HOST);

        invalidUrlNoSchema("user:<>@google.com:80/://", BusinessErrors.URL_INVALID_USERINFO);
        invalidUrlNoSchema("http://user:<>@google.com:80/://", BusinessErrors.URL_INVALID_USERINFO);

        invalidUrlNoSchema("<>:info@google.com:80/://", BusinessErrors.URL_INVALID_USERINFO);
        invalidUrlNoSchema("http://<>:info@google.com:80/://", BusinessErrors.URL_INVALID_USERINFO);

        invalidUrlNoSchema("https://info@google.com:80/://", BusinessErrors.URL_INVALID_SCHEMA);

        invalidUrlNoSchema("//google.com", BusinessErrors.URL_INVALID_HOST);
    }

    private void validUrl(String url) {
        assertTrue("Must be valid " + url, URLValidator.isValid(url));
    }

    private void invalidUrl(String url, BusinessErrors errorCode) {
        assertFalse("Must be invalid " + url, URLValidator.isValid(handler, url));
        assertEquals(errorCode, handler.getErrorCode());
    }

    private void validUrlNoSchema(String url) {
        String[] schemas = {"http", null};
        assertTrue("Must be valid " + url, URLValidator.isValid(url, schemas));
    }

    private void invalidUrlNoSchema(String url, BusinessErrors errorCode) {
        String[] schemas = {"http", null};
        assertFalse("Must be invalid " + url, URLValidator.isValid(handler, url, schemas));
        assertEquals(errorCode, handler.getErrorCode());
    }

    @Test
    public void testUrlTriggersWithMinus() throws Exception {
        assertInvalidURLTrigger("-google.com");
        assertInvalidURLTrigger("http://-google.com");
        assertInvalidURLTrigger("www.-google.com");
        assertInvalidURLTrigger("http://www.-google.com");
    }

    private void assertInvalidURLTrigger(String url) {
        assertFalse(URLValidator.isValidURLTrigger(handler, url));
        assertTrue(handler.getErrorCode() == BusinessErrors.URL_INVALID_HOST);
    }

    private static class TestUrlErrorHandler extends EmptyUrlErrorHandler {
        private BusinessErrors errorCode = BusinessErrors.GENERAL_ERROR;

        @Override
        public void invalidURL() {
            errorCode = BusinessErrors.URL_INVALID;
        }

        @Override
        public void invalidPort(String value) {
            errorCode = BusinessErrors.URL_INVALID_PORT;
        }

        @Override
        public void httpPortOnly() {
            errorCode = BusinessErrors.URL_INVALID_PORT;
        }

        @Override
        public void invalidUserinfo(String value) {
            errorCode = BusinessErrors.URL_INVALID_USERINFO;
        }

        @Override
        public void emptyHost() {
            errorCode = BusinessErrors.URL_INVALID_HOST;
        }

        @Override
        public void invalidHost(String value) {
            errorCode = BusinessErrors.URL_INVALID_HOST;
        }

        @Override
        public void invalidSchema(String[] schemas) {
            errorCode = BusinessErrors.URL_INVALID_SCHEMA;
        }

        public BusinessErrors getErrorCode() {
            return errorCode;
        }
    }
}
