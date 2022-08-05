package com.foros.rs.provider;

import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import javax.ws.rs.ext.Provider;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@Provider
public class TimestampStringReaderProvider implements StringReaderProvider {
    private static final DateTimeFormatter ISO_DATE_TIME_PARSER = ISODateTimeFormat.dateOptionalTimeParser();

    @Override
    public StringReader getStringReader(Class type, Type genericType, Annotation[] annotations) {
        if (type != Timestamp.class) {
            return null;
        }
        return new StringReader() {
            @Override
            public Object fromString(String value) {
                DateTime dateTime = ISO_DATE_TIME_PARSER.parseDateTime(value);
                return new Timestamp(dateTime.getMillis());
            }
        };
    }
}
