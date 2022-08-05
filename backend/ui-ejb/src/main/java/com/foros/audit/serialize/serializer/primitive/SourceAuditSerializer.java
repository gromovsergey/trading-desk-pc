package com.foros.audit.serialize.serializer.primitive;

import com.foros.model.currency.Source;

public class SourceAuditSerializer extends PrimitiveAuditSerializer {

    @Override
    protected String toString(Object value) {
        return Source.valueOf((Character) value).name();
    }

}
