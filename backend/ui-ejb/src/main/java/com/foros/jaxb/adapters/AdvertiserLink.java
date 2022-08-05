package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class AdvertiserLink extends EntityLink {

    private EntityLink agency;

    public AdvertiserLink() {
    }

    public AdvertiserLink(Long id) {
        super(id);
    }

    public EntityLink getAgency() {
        return agency;
    }

    public void setAgency(EntityLink agency) {
        this.agency = agency;
    }

}
