package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public abstract class AbstractLinkXmlAdapter extends XmlAdapter<EntityLink, Identifiable> {

    @Override
    public EntityLink marshal(Identifiable entity) throws Exception {
        return entity != null ? new EntityLink(entity.getId()) : null;
    }

    @Override
    public Identifiable unmarshal(EntityLink entityLink) throws Exception {
        return entityLink.getId() == null ? null : createInstance(entityLink.getId());
    }

    protected abstract Identifiable createInstance(Long id);

}
