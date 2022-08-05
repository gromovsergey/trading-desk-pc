package com.foros.session.security;

import com.foros.security.AccountRole;
import com.foros.util.mapper.Converter;

public class AccountTOConverter implements Converter<Object[], AccountTO> {

    @Override
    public AccountTO item(Object[] value) {
        return new AccountTO(
                ((Number) value[0]).longValue(),
                (String) value[1],
                (Character) value[2],
                AccountRole.valueOf(((Number) value[3]).intValue()),
                (String) value[4],
                ((Number) value[5]).longValue(),
                ((Number) value[6]).longValue()
        );
    }

}
