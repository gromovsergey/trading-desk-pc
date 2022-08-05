package com.foros.audit.serialize.serializer.primitive;

import com.foros.model.Status;

public class StatusAuditSerializer extends PrimitiveAuditSerializer {

    @Override
    protected String toString(Object value) {
        return Status.valueOf((Character) value).name();
    }

}
