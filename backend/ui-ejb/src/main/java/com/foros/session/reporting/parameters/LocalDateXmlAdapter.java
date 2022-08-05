package com.foros.session.reporting.parameters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {

    public static final DateTimeFormatter FORMATTER = ISODateTimeFormat.date();
    public static final DatatypeFactory DATATYPE_FACTORY = newDataTypeFactory();

    /**
     * All fields except year, month and date are ignored
     */
    @Override
    public LocalDate unmarshal(String v) throws Exception {
        if (StringUtil.isPropertyEmpty(v)) {
            return null;
        }

        XMLGregorianCalendar xmlCalendar = DATATYPE_FACTORY.newXMLGregorianCalendar(v);
        return new LocalDate(xmlCalendar.getYear(), xmlCalendar.getMonth(), xmlCalendar.getDay());
    }

    @Override
    public String marshal(LocalDate v) throws Exception {
        return FORMATTER.print(v);

    }

    private static DatatypeFactory newDataTypeFactory() {
        try {
            return DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
