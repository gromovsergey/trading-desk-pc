package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.creative.CreativeSize;

public class CreativeSizeXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new CreativeSize(id);
    }

}
