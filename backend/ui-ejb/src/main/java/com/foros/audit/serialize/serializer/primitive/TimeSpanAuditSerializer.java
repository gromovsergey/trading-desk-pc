package com.foros.audit.serialize.serializer.primitive;

import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;

public class TimeSpanAuditSerializer extends PrimitiveAuditSerializer {
    @Override
    protected String toString(Object value) {
        TimeSpan ts = (TimeSpan) value;
        String res;
        if (ts == null || ts.getValue() == null) {
            res = "removed";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(ts.getValue()).append(" ").append(ts.getUnit().toString().toLowerCase());
            sb.append(ts.getValue() == 1? "": "s");
            if (!TimeUnit.SECOND.equals(ts.getUnit())) {
                sb.append(" (").append(ts.getValueInSeconds()).append(" second");
                sb.append(ts.getValueInSeconds() == 1? ")": "s)");
            }
            res = sb.toString();
        }
        return res;
    }
}
