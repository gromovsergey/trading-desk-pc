package com.foros.jaxb.adapters;

import com.foros.model.Identifiable;
import com.foros.model.account.GenericAccount;
import com.foros.model.security.User;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class UserLinkXmlAdapter extends AbstractLinkXmlAdapter  {

    protected Identifiable createInstance(final Long id) {
        return new User(id);
    }

}
