package com.foros.jaxb.adapters;

import com.foros.util.StringUtil;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class MinutesToXsTimeAdapter extends XmlAdapter<String, Integer> {
    @Override
    public Integer unmarshal(String v) throws Exception {
        if (StringUtil.isPropertyEmpty(v)) {
            return null;
        }

        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(v);
        if (calendar.getYear() != DatatypeConstants.FIELD_UNDEFINED
                || calendar.getMonth() != DatatypeConstants.FIELD_UNDEFINED
                || calendar.getDay() != DatatypeConstants.FIELD_UNDEFINED) {
            throw new IllegalArgumentException("Only hours and minutes are allowed");
        }

        return calendar.getHour() * 60 + calendar.getMinute();
    }

    @Override
    public String marshal(Integer v) throws Exception {
        if (v == null) {
            return null;
        }

        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        calendar.setHour(v / 60);
        calendar.setMinute(v % 60);
        calendar.setSecond(0);
        return calendar.toXMLFormat();
    }
}
