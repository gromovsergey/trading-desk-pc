package com.foros.rs.schema;

import org.junit.Assert;
import org.junit.Test;

public class PathNodeTest extends Assert {

    @Test
    public void testMakePath() {
        PathNode root = new PathNode("root");
        PathNode list = root.getChild("elements");
        list.getChild("element");
        list.getChild("other");
        PathNode node = list.getChild("element").getChild("qwerty");
        assertEquals("root.elements[1].qwerty", node.fullPath("."));
        assertEquals("elements[1].qwerty", node.pathFrom(root, "."));
    }
}
