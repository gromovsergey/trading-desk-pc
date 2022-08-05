package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.channel.CategoryChannel;

public class CategoryLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        CategoryChannel categoryChannel = new CategoryChannel();
        categoryChannel.setId(id);
        return categoryChannel;
    }

}