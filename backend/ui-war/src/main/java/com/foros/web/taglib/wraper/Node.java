package com.foros.web.taglib.wraper;

public class Node {
    private String content;
    private NodeType type;

    public Node(String content, NodeType type) {
        this.content = content;
        this.type = type;
    }

    public NodeType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
