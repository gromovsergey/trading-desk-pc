package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractXmlAdapter extends XmlAdapter<EntityLink, Identifiable> {

    @Override
    public EntityLink marshal(Identifiable entity) throws Exception {
        return entity == null ? null : new EntityLink(entity.getId());
    }

    @Override
    public Identifiable unmarshal(EntityLink entityLink) throws Exception {
        return createInstance(entityLink.getId());
    }

    protected abstract Identifiable createInstance(Long id);

}