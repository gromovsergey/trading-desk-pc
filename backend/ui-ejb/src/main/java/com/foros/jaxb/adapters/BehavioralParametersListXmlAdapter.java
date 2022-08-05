package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.channel.BehavioralParametersList;

public class BehavioralParametersListXmlAdapter extends AbstractLinkXmlAdapter  {

    protected Identifiable createInstance(final Long id) {
        BehavioralParametersList parametersList = new BehavioralParametersList();
        parametersList.setId(id);
        return parametersList;
    }

}
