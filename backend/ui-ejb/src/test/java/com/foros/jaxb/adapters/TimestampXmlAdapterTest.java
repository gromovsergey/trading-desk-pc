package com.foros.jaxb.adapters;

import group.Jaxb;
import group.Unit;
import java.sql.Timestamp;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ Unit.class, Jaxb.class })
public class TimestampXmlAdapterTest extends Assert {

    @Test
    public void testConversion() throws Exception {
        TimestampXmlAdapter adapter = new TimestampXmlAdapter();

        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar("2011-01-18T11:50:27.10156+03:00");

        Timestamp ts = adapter.unmarshal(xmlDate);
        assertEquals(101560000, ts.getNanos());

        XMLGregorianCalendar xmlDate2 = adapter.marshal(ts);
        assertEquals(xmlDate, xmlDate2);
    }
}
