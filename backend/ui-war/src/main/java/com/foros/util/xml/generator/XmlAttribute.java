package com.foros.util.xml.generator;

/**
 * Author: Boris Vanin
 */
public class XmlAttribute implements Textable {

    private String name;
    private String value;

    public XmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String asString() {
        return name + "='" + value + "'";
    }

}
