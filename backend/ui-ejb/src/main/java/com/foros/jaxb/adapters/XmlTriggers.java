package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class XmlTriggers {

    List<String> list;

    public XmlTriggers() {
    }

    public XmlTriggers(List<String> list) {
        this.list = list;
    }

    @XmlElement(name = "t")
    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
