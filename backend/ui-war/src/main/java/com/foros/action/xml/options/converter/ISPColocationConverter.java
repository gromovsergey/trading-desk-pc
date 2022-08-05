package com.foros.action.xml.options.converter;

import com.foros.session.campaign.ISPColocationTO;

public class ISPColocationConverter extends AbstractConverter<ISPColocationTO> {

    public ISPColocationConverter() {
        super(false);
    }

    @Override
    protected String getName(ISPColocationTO value) {
        return value.getFullName();
    }

    @Override
    protected String getValue(ISPColocationTO value) {
        return value.getId() == null ? "" : value.getId().toString();
    }
}