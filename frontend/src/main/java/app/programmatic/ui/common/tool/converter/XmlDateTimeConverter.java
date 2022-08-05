package app.programmatic.ui.common.tool.converter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XmlDateTimeConverter {
    private static final int NANO_MULTIPLIER = 1000000000;

    public static XMLGregorianCalendar convertTimestamp(Timestamp src, String zoneName) {
        if (src == null) {
            return null;
        }

        return convertDateTime(src.toLocalDateTime(), zoneName);
    }

    public static XMLGregorianCalendar convertDateTime(LocalDateTime dateTime, String zoneName) {
        if (dateTime == null) {
            return null;
        }

        GregorianCalendar calendar = GregorianCalendar.from(dateTime.atZone(ZoneId.of(zoneName)));
        try {
            XMLGregorianCalendar result = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
            result.setFractionalSecond(BigDecimal.valueOf(dateTime.getNano()).movePointLeft(9));
            return result;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalDateTime convertDateTime(XMLGregorianCalendar xmlDateTime) {
        if (xmlDateTime == null) {
            return null;
        }

        ZonedDateTime zonedDateTime = xmlDateTime.toGregorianCalendar().toZonedDateTime();
        LocalDateTime result = LocalDateTime.of(
            zonedDateTime.getYear(),
            zonedDateTime.getMonth().getValue(),
            zonedDateTime.getDayOfMonth(),
            zonedDateTime.getHour(),
            zonedDateTime.getMinute(),
            zonedDateTime.getSecond(),
            xmlDateTime.getFractionalSecond().movePointRight(9).intValue()
        );
        return result;
    }

    public static Long convertToEpochTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        ZoneOffset systemOffset = ZoneOffset.systemDefault().getRules().getOffset(localDateTime);
        return localDateTime.toEpochSecond(systemOffset) * NANO_MULTIPLIER + localDateTime.getNano();
    }

    public static Long convertToEpochTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return (timestamp.getTime() / 1000) * NANO_MULTIPLIER + timestamp.getNanos();
    }

    public static Long convertToEpochTime(XMLGregorianCalendar xmlDateTime) {
        if (xmlDateTime == null) {
            return null;
        }

        return convertToEpochTime(convertDateTime(xmlDateTime));
    }

    public static Timestamp convertEpochToTimestamp(Long nanos) {
        if (nanos == null) {
            return null;
        }

        long secondsFromEpoch = nanos / NANO_MULTIPLIER;
        Timestamp result = new Timestamp(secondsFromEpoch * 1000);
        result.setNanos((int)(nanos - secondsFromEpoch * NANO_MULTIPLIER));
        return result;
    }

    public static XMLGregorianCalendar convertEpoch(Long nanos, String zoneName) {
        if (nanos == null) {
            return null;
        }

        Timestamp timestamp = convertEpochToTimestamp(nanos);
        return convertTimestamp(timestamp, zoneName);
    }
}
