package com.foros.audit.serialize.serializer.primitive;

import java.math.BigDecimal;

public class BigDecimalAuditSerializer extends PrimitiveAuditSerializer {
    @Override
    protected String toString(Object value) {
        return ((BigDecimal)value).toPlainString();
    }
}
