package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.*;

@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class EntityLink {

    private Long id;

    public EntityLink() {
    }

    public EntityLink(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
