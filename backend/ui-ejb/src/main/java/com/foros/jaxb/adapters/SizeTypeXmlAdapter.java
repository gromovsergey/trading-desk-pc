package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.creative.SizeType;

public class SizeTypeXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        SizeType sizeType = new SizeType();
        sizeType.setId(id);
        return sizeType;
    }
}
