package com.foros.session.bulk;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class OperationsResult {

    private List<Long> ids;

    public OperationsResult() {
    }

    public OperationsResult(List<Long> result) {
        ids = result;
    }

    @XmlElement(name = "id")
    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

}
