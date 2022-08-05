package com.foros.rs.schema;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class PathNode {

    private PathNode parent;
    private String name;
    private int counter;
    private Set<PathNode> children;

    public PathNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getCounter() {
        return counter;
    }

    public PathNode getChild(String name) {
        PathNode node = null;
        if (children != null) {
            for (PathNode child : children) {
                if (name.equals(child.name)) {
                    node = child;
                    break;
                }
            }
        } else {
            children = new HashSet<PathNode>();
        }
        if (node == null) {
            node = new PathNode(name);
            node.parent = this;
            children.add(node);
        }
        node.counter++;
        return node;
    }

    public PathNode getParent() {
        children = null;
        return parent;
    }

    public String fullPath(String delimiter) {
        return pathFrom(null, delimiter);
    }

    public String pathFrom(PathNode from, String delimiter) {
        Stack<String> path = new Stack<String>();
        for (PathNode node = this; node != from && node != null; node = node.parent) {
            int index = node.counter - 1;
            if (isPlural(node)) {
                node = node.parent;
            } else if (index == 0) {
                index = -1;
            }
            String name = node.name;
            if (index >= 0) {
                name = name + '[' + index + ']';
            }
            path.push(name);
        }
        StringBuilder builder = new StringBuilder();
        while (!path.empty()) {
            builder.append(path.pop());
            if (!path.empty()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    private static boolean isPlural(PathNode node) {
        return node.parent != null && node.parent.name.equals(node.name + 's');
    }
}
