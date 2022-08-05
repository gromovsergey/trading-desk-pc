package com.foros.action.xml.options.converter;

import com.foros.session.security.AccountTO;
import com.foros.util.PairUtil;

public class AccountTOConverter extends AbstractConverter<AccountTO> {
    public AccountTOConverter() {
        super(false);
    }

    @Override
    protected String getName(AccountTO value) {
        return value.getName();
    }

    @Override
    protected String getValue(AccountTO value) {
        return PairUtil.createAsString(value.getId(), value.getRole().getWebName());
    }
}