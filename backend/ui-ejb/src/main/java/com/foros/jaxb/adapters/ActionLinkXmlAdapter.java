package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.action.Action;

public class ActionLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        return new Action(id);
    }
}
