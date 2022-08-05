package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeXmlAdapter extends XmlAdapter<String, Date> {
    private static final DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new Error(e);
        }
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return null;
        }

        v = StringUtil.trimProperty(v);
        XMLGregorianCalendar calendar = datatypeFactory.newXMLGregorianCalendar(v);

        if (calendar.getYear() == DatatypeConstants.FIELD_UNDEFINED
                || calendar.getMonth() == DatatypeConstants.FIELD_UNDEFINED
                || calendar.getDay() == DatatypeConstants.FIELD_UNDEFINED) {
            throw new JAXBException("Year, Month and Day can't be omitted");
        }

        return calendar.toGregorianCalendar().getTime();
    }

    @Override
    public String marshal(Date v) throws Exception {
        if (v == null) {
            return null;
        }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(v);
        return DatatypeConverter.printDateTime(cal);
    }
}