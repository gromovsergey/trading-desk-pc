package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.session.bulk.IdNameTO;

public class IdNameTOLinkXmlAdapter extends AbstractLinkXmlAdapter {
    @Override
    protected Identifiable createInstance(Long id) {
        return new IdNameTO(id, null);
    }
}
