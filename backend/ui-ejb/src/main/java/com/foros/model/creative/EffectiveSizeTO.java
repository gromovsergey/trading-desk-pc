package com.foros.model.creative;

import com.foros.model.Identifiable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "id",
        "width",
        "height"
})
@XmlAccessorType(XmlAccessType.NONE)
public class EffectiveSizeTO implements Identifiable {
    private Long id;
    private Long width;
    private Long height;

    public EffectiveSizeTO() {}

    public EffectiveSizeTO(Long id) {
        this.id = id;
    }

    @XmlElement
    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @Override
    @XmlElement
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement
    public Long getWidth() {
        return width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }
}
