package com.foros.model.restriction;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name = "predicates")
@XmlType(propOrder = {
        "predicates"
})
public class Predicates {
    private List<Boolean> predicates;

    public Predicates() {
    }

    public Predicates(List<Boolean> predicates) {
        this.predicates = predicates;
    }

    @XmlElement(name = "predicate")
    public List<Boolean> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<Boolean> predicates) {
        this.predicates = predicates;
    }
}
