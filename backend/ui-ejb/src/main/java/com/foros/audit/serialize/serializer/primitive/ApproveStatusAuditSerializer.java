package com.foros.audit.serialize.serializer.primitive;

import com.foros.model.ApproveStatus;

public class ApproveStatusAuditSerializer extends PrimitiveAuditSerializer {
    @Override
    protected String toString(Object value) {
        return ApproveStatus.valueOf((Character) value).toString();
    }
}