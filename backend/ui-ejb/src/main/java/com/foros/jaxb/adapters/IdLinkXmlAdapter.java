package com.foros.jaxb.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IdLinkXmlAdapter extends XmlAdapter<EntityLink, Long> {

    @Override
    public EntityLink marshal(Long id) throws Exception {
        return id != null ? new EntityLink(id) : null;
    }

    @Override
    public Long unmarshal(EntityLink entityLink) throws Exception {
        return entityLink != null ? entityLink.getId() : null;
    }
}
