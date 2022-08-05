package com.foros.web.taglib.wraper;

import java.util.List;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DummyHtmlParserTest {
    @Test
    @Category(Unit.class)
    public void parse() {
        String html = "1234 <div> sadasd <span> 1232 </span> <br/> </div> qwerty <a href=''>foo</a>";
        HtmlParser parser = new HtmlParser();
        parser.parse(html);
        List<Node> nodes = parser.getNodes();
        assertNotNull(nodes);
        assertEquals(4, nodes.size());

        assertEquals(NodeType.TEXT, nodes.get(0).getType());
        assertTrue(nodes.get(0).getContent().contains("1234"));

        assertEquals(NodeType.TAG, nodes.get(1).getType());
        assertTrue(nodes.get(1).getContent().contains("<div> sadasd <span> 1232 </span> <br/> </div>"));

        assertEquals(NodeType.TEXT, nodes.get(2).getType());
        assertTrue(nodes.get(2).getContent().contains("qwerty"));

        assertEquals(NodeType.TAG, nodes.get(3).getType());
        assertTrue(nodes.get(3).getContent().contains("<a href=''>foo</a>"));
    }
}
