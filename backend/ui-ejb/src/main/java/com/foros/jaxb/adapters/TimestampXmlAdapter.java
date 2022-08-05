package com.foros.jaxb.adapters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class TimestampXmlAdapter extends XmlAdapter<XMLGregorianCalendar, Timestamp> {
    private static ThreadLocal<DatatypeFactory> datatypeFactoryThreadLocal = new ThreadLocal<>();

    @Override
    public XMLGregorianCalendar marshal(Timestamp v) throws Exception {
        try {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(v.getTime());
            XMLGregorianCalendar xmlDate = getDatatypeFactory().newXMLGregorianCalendar(calendar);
            xmlDate.setFractionalSecond(BigDecimal.valueOf(v.getNanos()).scaleByPowerOfTen(-9).setScale(6));
            return xmlDate;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private DatatypeFactory getDatatypeFactory() throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = datatypeFactoryThreadLocal.get();
        if (datatypeFactory == null) {
            datatypeFactory = DatatypeFactory.newInstance();
            datatypeFactoryThreadLocal.set(datatypeFactory);
        }
        return datatypeFactory;
    }

    @Override
    public Timestamp unmarshal(XMLGregorianCalendar v) throws Exception {
        long timeInMillis = v.toGregorianCalendar().getTimeInMillis();
        Timestamp ts = new Timestamp(timeInMillis);
        ts.setNanos(v.getFractionalSecond().scaleByPowerOfTen(9).intValue());
        return ts;
    }

}
