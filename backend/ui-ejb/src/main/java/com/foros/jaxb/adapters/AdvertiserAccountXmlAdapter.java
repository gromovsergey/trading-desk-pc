package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.account.AdvertiserAccount;

public class AdvertiserAccountXmlAdapter extends AbstractXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        AdvertiserAccount account = new AdvertiserAccount();
        account.setId(id);
        return account;
    }

}
