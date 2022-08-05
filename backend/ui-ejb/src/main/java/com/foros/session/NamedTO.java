package com.foros.session;

import com.foros.model.IdNameEntity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


@XmlAccessorType(XmlAccessType.NONE)
public class NamedTO extends IdentifiableTO implements IdNameEntity, Serializable, Comparable<NamedTO> {

    private String name;

    public NamedTO() {
    }

    /**
     * Creates a new instance of NamedTO
     */
    public NamedTO(Long id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int compareTo(NamedTO o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return String.format("[id=%d,name=%s]", getId(), name);
    }
}
