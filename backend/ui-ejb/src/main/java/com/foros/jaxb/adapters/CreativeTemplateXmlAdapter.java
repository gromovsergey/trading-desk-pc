package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.template.CreativeTemplate;

public class CreativeTemplateXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new CreativeTemplate(id);
    }

}
