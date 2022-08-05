package com.foros.validation.constraint.violation;

import com.foros.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Path implements Iterable<Path.Node>, Serializable {

    private static final Path EMPTY_PATH = new Path();

    public static class Node implements Serializable {

        private String name;
        private Integer index;

        private Node(String name, Integer index) {
            this.name = name;
            this.index = index;
        }

        private Node(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Integer getIndex() {
            return index;
        }

        public boolean isArray() {
            return index != null;
        }

        @Override
        public String toString() {
            return name + (isArray() ? "[" + index + "]" : "");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Node node = (Node) o;

            return node.toString().equals(toString());
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    private final List<Node> nodes;
    private String toStringCache;

    @Override
    public Iterator<Node> iterator() {
        return Collections.unmodifiableList(nodes).iterator();
    }

    private Path() {
        this.nodes = Collections.emptyList();
    }

    private Path(Path path, Path childNodes) {
        this.nodes = new ArrayList<Node>(path.nodes.size() + childNodes.nodes.size());
        this.nodes.addAll(path.nodes);
        this.nodes.addAll(childNodes.nodes);
    }

    private Path(Path path, String... childNodes) {
        this.nodes = new ArrayList<Node>(path.nodes.size() + childNodes.length);
        this.nodes.addAll(path.nodes);
        for (String childNode : childNodes) {
            if (childNode != null) {
                this.nodes.add(new Node(childNode));
            }
        }
    }

    private Path(Path path, String node) {
        this.nodes = new ArrayList<Node>(path.nodes.size() + 1);
        this.nodes.addAll(path.nodes);
        this.nodes.add(new Node(node));
    }

    private Path(Path path, String node, Integer index) {
        this.nodes = new ArrayList<Node>(path.nodes.size() + 1);
        this.nodes.addAll(path.nodes);
        this.nodes.add(new Node(node, index));
    }

    public Path add(Path path) {
        if (path.isEmpty()) {
            return this;
        } else {
            return new Path(this, path);
        }
    }

    public Path add(String node) {
        if (StringUtil.isPropertyEmpty(node)) {
            return this;
        } else {
            return new Path(this, node);
        }
    }

    public Path add(String node, Integer index) {
        node = StringUtil.isPropertyEmpty(node) ? "" : node;
        if (node.isEmpty() && index == null) {
            return this;
        } else {
            return new Path(this, node, index);
        }
    }

    public Path add(String... path) {
        if (path.length == 0) {
            return this;
        } else {
            return new Path(this, path);
        }
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public static Path fromArray(String[] parentPath) {
        return new Path(EMPTY_PATH, parentPath);
    }

    public static Path fromString(String path) {
        return fromArray(parsePath(path));
    }

    public Path subtract(Path path) {
        String minuend = toString();
        String subtrahend = path.toString();
        if (!path.isEmpty() && minuend.startsWith(subtrahend)) {
            return fromString(minuend.substring(subtrahend.length() + 1));
        } else {
            return this;
        }
    }

    private static String[] parsePath(String path) {
        return path.split("\\."); // todo!!!
    }

    public static Path empty() {
        return EMPTY_PATH;
    }

    @Override
    public String toString() {
        if (toStringCache != null) {
            return toStringCache;
        }

        StringBuilder builder = new StringBuilder();

        boolean first = true;

        for (Node node : nodes) {
            if (first) {
                first = false;
            } else if (!node.getName().isEmpty()) {
                builder.append(".");
            }

            builder.append(node.toString());
        }

        toStringCache = builder.toString();
        return toStringCache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Path path = (Path) o;

        return path.toString().equals(toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
