package com.foros.util;

import com.foros.util.resource.ResourceHelper;
import com.foros.web.taglib.XslTagHelper;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNotNull;

public class XslTagHelperTest {
    private final static String AUDIT_XLS = "com/foros/web/audit/audit.xsl";
    private final static String TEST_XML = "com/foros/util/testLoginRecord.xml";

    @Test
    @Category(Unit.class)
    public void transform() throws ParserConfigurationException, SAXException, IOException {
        String content = ResourceHelper.readContent(TEST_XML);

        String result = XslTagHelper.transform(content, AUDIT_XLS);
        assertNotNull(result);
    }
}
