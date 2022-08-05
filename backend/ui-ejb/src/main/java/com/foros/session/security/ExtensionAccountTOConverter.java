package com.foros.session.security;

import com.foros.security.AccountRole;
import com.foros.util.mapper.Converter;

public class ExtensionAccountTOConverter implements Converter<Object[], ExtensionAccountTO> {

    @Override
    public ExtensionAccountTO item(Object[] value) {
        return new ExtensionAccountTO(
            ((Number) value[0]).longValue(),
            (String) value[1],
            (Character) value[2],
            AccountRole.valueOf(((Number) value[3]).intValue()),
            (String) value[4],
            ((Number) value[5]).longValue(),
            ((Number) value[6]).longValue(),
            ((String) value[7]),
            value[8] != null ? ((Number) value[8]).longValue() : null);
    }
}
