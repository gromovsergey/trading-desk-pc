package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.site.Site;

public class SiteLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new Site(id);
    }
}
