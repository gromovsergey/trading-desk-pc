package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.site.Tag;

public class TagLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new Tag(id);
    }
}
