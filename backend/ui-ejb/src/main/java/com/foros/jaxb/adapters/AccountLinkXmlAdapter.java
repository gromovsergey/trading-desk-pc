package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.account.GenericAccount;

public class AccountLinkXmlAdapter extends AbstractLinkXmlAdapter {

    @Override
    protected Identifiable createInstance(final Long id) {
        GenericAccount genericAccount = new GenericAccount();
        genericAccount.setId(id);
        return genericAccount;
    }

}
