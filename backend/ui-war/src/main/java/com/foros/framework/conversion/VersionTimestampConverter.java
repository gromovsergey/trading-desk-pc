package com.foros.framework.conversion;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import java.sql.Timestamp;
import java.util.Map;

public class VersionTimestampConverter extends SingleValueBaseTypeConverter {

    @Override
    public Object convertFromString(Map<String, Object> context, String value, Class toClass) {
        String[] array = value.split("[/]");
        Timestamp version = new Timestamp(Long.parseLong(array[0]));
        version.setTime(Long.parseLong(array[0]));
        version.setNanos(Integer.parseInt(array[1]));
        return version;
    }

    @Override
    public String convertToString(Map<String, Object> context, Object o) {
        if (!(o instanceof Timestamp)) {
            throw new TypeConversionException("Date object expected, actual is " + o);
        }

        Timestamp time = (Timestamp) o;
        return time.getTime() + "/" + time.getNanos();
    }
}
