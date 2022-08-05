package com.foros.web.taglib.wraper;

import java.util.ArrayList;
import java.util.List;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.visitors.NodeVisitor;

public class HtmlParser {
    private List<Node> nodes;

    public HtmlParser parse(String html) {
        Parser parser = new Parser();
        TagSplitterVisitor visitor = new TagSplitterVisitor();
        try {
            parser.setInputHTML(html);
            parser.visitAllNodesWith(visitor);
        } catch (Exception e) {
            nodes = null;
            throw new RuntimeException(e);
        }
        return this;
    }

    public List<Node> getNodes() {
        if (nodes == null) {
            throw new IllegalStateException("Call parse(String html) first");
        }
        return nodes;
    }

    private class TagSplitterVisitor extends NodeVisitor {
        private TagSplitterVisitor() {
            super(false, true);
        }

        @Override
        public void beginParsing() {
            nodes = new ArrayList<Node>();
        }

        @Override
        public void visitStringNode(Text string) {
            addNode(string, NodeType.TEXT);
        }

        @Override
        public void visitTag(Tag tag) {
            addNode(tag, NodeType.TAG);
        }

        public List<Node> getNodes() {
            return nodes;
        }

        private void addNode(org.htmlparser.Node string, NodeType type) {
            Node node = new Node(string.toHtml(), type);
            nodes.add(node);
        }
    }
}
