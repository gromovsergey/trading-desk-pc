package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.creative.Creative;

public class CreativeXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new Creative(id);
    }
}
