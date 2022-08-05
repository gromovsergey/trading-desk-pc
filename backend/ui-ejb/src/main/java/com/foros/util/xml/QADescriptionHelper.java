package com.foros.util.xml;

import com.foros.util.StringUtil;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class QADescriptionHelper {

    public static final JAXBContext CONTEXT = newContext();

    private static JAXBContext newContext()  {
        try {
            return JAXBContext.newInstance(QADescriptionRoot.class);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private QADescriptionHelper() {
    }

    public static QADescription fromXML(String xml) {
        if (xml == null) {
            return null;
        }

        QADescription description = null;
        if (StringUtil.isPropertyNotEmpty(xml)) {
            try {
                Unmarshaller unmarshaller = CONTEXT.createUnmarshaller();
                description = (QADescription) unmarshaller.unmarshal(new StringReader(xml));
            } catch (JAXBException e) {
                description = new QADescriptionError();
            }
        }
        return description;
    }

    public static String toXML(QADescription qaDescription) {
        if (qaDescription == null) {
            return null;
        }

        StringWriter out = new StringWriter();
        try {
            Marshaller marshaller = CONTEXT.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.marshal(qaDescription, out);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("It is not possible to transform to XML", e);
        }
        return out.toString();
    }
}
