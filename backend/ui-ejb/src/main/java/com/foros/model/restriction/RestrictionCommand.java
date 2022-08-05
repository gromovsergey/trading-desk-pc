package com.foros.model.restriction;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;


@XmlType(propOrder = {
        "name",
        "params"
})
public class RestrictionCommand {
    private String name;
    private List<RestrictionParameter> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "params")
    @XmlElement(name = "param")
    public List<RestrictionParameter> getParams() {
        return params;
    }

    public void setParams(List<RestrictionParameter> params) {
        this.params = params;
    }
}
