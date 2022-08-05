package com.foros.util.xml.generator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Boris Vanin
 */
class XmlElement implements Textable {

    private XmlElement parent;
    private String name;

    private String text;

    private Collection<XmlAttribute> attributes = new ArrayList<XmlAttribute>();
    private Collection<XmlElement> childs = new ArrayList<XmlElement>();

    XmlElement(XmlElement parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public XmlElement parent() {
        return parent;
    }

    public void addChild(XmlElement child) {
        childs.add(child);
    }

    public void addAttribute(XmlAttribute attribute) {
        attributes.add(attribute);
    }

    public XmlElement getParent() {
        return parent;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String asString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<").append(name);

        for (XmlAttribute attribute : attributes) {
            builder.append(" ").append(attribute.asString());
        }

        if (childs.isEmpty() && text == null) {
            builder.append("/>");
        } else {
            builder.append(">");
            for (XmlElement child : childs) {
                builder.append(child.asString());
            }

            if (text != null) {
                builder.append(text);
            }

            builder.append("</").append(name).append(">");
        }

        return builder.toString();
    }

}
