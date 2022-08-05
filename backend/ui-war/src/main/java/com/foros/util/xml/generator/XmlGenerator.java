package com.foros.util.xml.generator;

/**
 * Author: Boris Vanin
 */
public class XmlGenerator implements Textable {

    private XmlElement root = null;
    private XmlElement active = null;
    private boolean childMode = false;

    public XmlGenerator root(String name) {
        if (root != null) {
            throw new GenerationException("Document can't have many root elements");
        }

        this.active = this.root = new XmlElement(null, name);
        return this;
    }

    public XmlGenerator attribute(String name, String value) {
        this.active.addAttribute(new XmlAttribute(name, value));
        return this;
    }

    public XmlGenerator childs() {
        this.childMode = true;
        return this;
    }

    public XmlGenerator end() {
        active = active.getParent();
        return this;
    }

    public XmlGenerator tag(String name) {
        if (childMode) {
            XmlElement child = new XmlElement(this.active, name);
            this.active.addChild(child);
            this.active = child;
            childMode = false;
        } else {
            XmlElement parent = this.active.getParent();
            this.active = new XmlElement(parent, name);
            parent.addChild(this.active);
        }

        return this;
    }

    public XmlGenerator text(Object content) {
        this.active.setText(content.toString());
        return this;
    }

    public String asString() {
        return root.asString();
    }

    public static void main(String[] args) {
        XmlGenerator generator = new XmlGenerator();
        String xml = generator.
                root("root").
                attribute("attr1", "value1").
                childs().
                tag("subelement1").
                attribute("blabla", "value").
                attribute("moblabla", "value2").
                tag("subelement2").
                childs().
                tag("value").text("blablablalba").
                end().
                end().
                asString();

        System.out.println(xml);
    }

}
