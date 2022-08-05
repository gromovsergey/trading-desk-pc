package com.foros.session.bulk;

import com.foros.model.EntityBase;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;

@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Operation<T extends EntityBase> {

    private T entity;
    private OperationType operationType;

    @XmlElementRef
    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    @XmlAttribute(name = "type")
    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }
}
