package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.creative.CreativeCategory;

public class CreativeCategoryXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new CreativeCategory(id);
    }

}
