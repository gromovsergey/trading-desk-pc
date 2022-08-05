package com.foros.util.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "text")
public class QADescriptionText implements QADescription {

    private String text;

    public QADescriptionText() {
    }

    public QADescriptionText(String text) {
        this.text = text;
    }

    @XmlValue
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
