package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.isp.Colocation;

public class ColocationLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        Colocation colocation = new Colocation();
        colocation.setId(id);
        return colocation;
    }
}
