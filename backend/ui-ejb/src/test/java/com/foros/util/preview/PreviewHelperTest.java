package com.foros.util.preview;

import com.foros.AbstractUnitTest;
import com.foros.model.template.CreativeInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import group.Resource;
import group.Unit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jparsec.error.ParserException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Resource.class })
public class PreviewHelperTest extends AbstractUnitTest {

    public static final String TEST_VALUE_RU = "\u0414\u0430";

    @Test
    public void replaceTokensWithException() throws IOException {
        String src = ";W.src=O[E];this.U.push(W);}},g:function(g){return g.replace(/^##[^#]+##$/,'');},d:function(t6){},V:function(){return parseInt(Math.random()*10000000);}});zd47ft.o(zd47ft,{fs:function(G){if(G.tagName=='IFRAME'){G.frameBorder=0;G.marginWidth=0;G.marginHeight=0;G";
        try {
            PreviewHelper.generateTextPreview(new HashMap<String, String>(), new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)));
            fail("Exception is expected");
        } catch (Exception e) {
            // expected;
        }
    }

    @Test
    public void replaceTokens() throws IOException {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("RANDOM", "123");
        replacements.put("CLICK", "http://google.com");
        String[][] test_noPrefix = {
                { "##RANDOM## test ##CLICK## template ",
                        "123 test http://google.com template "
                },
                { "second ##XINFOPSID=0## test ##RANDOM=##",
                        "second 0 test 123"
                },
                { "###RANDOM## test ####CLICK## template ",
                    "#123 test ##http://google.com template "
                },
                { "second ###XINFOPSID=0## test #####RANDOM=##",
                    "second #0 test ###123"
                }
        };

        doTest(replacements, test_noPrefix);

        // Test for Prefix with UTF-8
        replacements.put("RANDOM", "ABc123&<>\'\")");
        replacements.put("CLICK", "http://google.com");
        String[][] test_UTF = {
                { "##utf8:RANDOM## test ##CLICK## template",
                        "ABc123&<>'\") test http://google.com template"
                }
        };

        doTest(replacements, test_UTF);

        // Test for Prefix with MIME_URL
        replacements.put("RANDOM", "ABc123&<>\'\")");
        replacements.put("CLICK", "http://google.com?search=" + TEST_VALUE_RU);
        String[][] test_MIME_URL = {
                { "##RANDOM## test ##mime-url:CLICK## template",
                        "ABc123&<>\'\") test http%3A%2F%2Fgoogle.com%3Fsearch%3D%D0%94%D0%B0 template"
                }
        };

        doTest(replacements, test_MIME_URL);

        // Test for Prefix with XML
        replacements.put("RANDOM", "ABc123&<>\'\")");
        replacements.put("CLICK", "http://google.com");
        String[][] test_XML = {
                { "##xml:RANDOM## test ##CLICK## template",
                        "ABc123&amp;&lt;&gt;&apos;&quot;) test http://google.com template"
                }
        };

        doTest(replacements, test_XML);

        // Test for Prefix with JS
        replacements.put("RANDOM", "123&\n4ABc\r)");
        replacements.put("CLICK", "http://google.com");
        String[][] test_JS = {
                { "##js:RANDOM## test ##CLICK## template",
                        "123&\\xA4ABc\\xD) test http://google.com template"
                }
        };

        doTest(replacements, test_JS);

        // Test for Prefix with JS-UNICODE
        replacements.put("RANDOM", "123&ABc)");
        replacements.put("CLICK", "http://google.com");
        String[][] test_JS_UNICODE = {
                { "##js-unicode:RANDOM## test ##CLICK## template",
                        "123\\u0026ABc\\u0029 test http://google.com template"
                },
                { "third ##XINFOPSID=0## test ##RANDOM=##",
                        "third 0 test 123&ABc)"
                }
        };

        doTest(replacements, test_JS_UNICODE);
    }

    private void doTest(Map<String, String> replacements, String[][] test_noPrefix) throws IOException {
        for (String[] aTest : test_noPrefix) {
            String input = aTest[0];
            try {
                String res = PreviewHelper.generateTextPreview(replacements, new ByteArrayInputStream(input.getBytes()));
                assertEquals("Failed: " + input, aTest[1], res);
            } catch (ParserException e) {
                fail("Failed: " + input + "\n" + e);
            }
        }
    }

    @Test
    public void creativeJsonToken() {
        String result;

        result = PreviewHelper.generateCreativeJsonOptionValue(new LinkedHashMap<String, String>());
        assertEquals("[{}]", result);

        result = PreviewHelper.generateCreativeJsonOptionValue(generateOptionValues(1));
        assertEquals("[{\"token1\":\"value1\"}]", result);

        result = PreviewHelper.generateCreativeJsonOptionValue(generateOptionValues(1, 2));
        assertEquals("[{\"token1\":\"value1\",\"token2\":\"value2\"}]", result);

        List<CreativeInfo> creativeInfoList = new LinkedList<>();
        creativeInfoList.add(new CreativeInfo(generateOptionValues(1)));
        creativeInfoList.add(new CreativeInfo(generateOptionValues(2, 3)));
        creativeInfoList.add(new CreativeInfo());
        Map<String, String> optionValues = PreviewHelper.addCreativeJsonOptionValue(generateOptionValues(4, 5, 6), creativeInfoList);
        assertEquals(optionValues.size(), 4);
        assertTrue(optionValues.containsKey(PreviewHelper.CREATIVES_JSON_TOKEN));
        assertEquals("[{\"token1\":\"value1\"},{\"token2\":\"value2\",\"token3\":\"value3\"},{}]", optionValues.get(PreviewHelper.CREATIVES_JSON_TOKEN));
    }

    private Map<String, String> generateOptionValues(int... numbers) {
        Map<String, String> optionValues = new LinkedHashMap<>();
        for (int number: numbers) {
            optionValues.put("token" + number, "value" + number);
        }
        return optionValues;
    }

    @Test
    public void testEscaping() {
        assertEscaped('\'');
        assertEscaped('\"');
        assertEscaped('\\');
        assertEscaped('\n');
        assertEscaped('\r');
        assertEscaped('<');
    }

    private void assertEscaped(char toTest) {
        HashMap<String, String> map = new HashMap<>();
        map.put("v", String.valueOf(toTest));
        String json = PreviewHelper.generateCreativeJsonOptionValue(map);
        String code = StringUtils.leftPad(Integer.toHexString(toTest).toUpperCase(), 4, '0');
        assertEquals("Test for " + toTest, "[{\"v\":\"\\u" + code + "\"}]", json);
    }

    @Test
    public void testInternational() {
        HashMap<String, String> map = new LinkedHashMap<>();
        map.put("ru", "русский");
        map.put("zh", "中国的");
        String json = PreviewHelper.generateCreativeJsonOptionValue(map);
        assertEquals("[{\"ru\":\"русский\",\"zh\":\"中国的\"}]", json);
    }
}
