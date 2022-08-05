package com.foros.rs.provider;

import com.foros.config.Config;
import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.session.ServiceLocator;
import com.foros.util.StringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Provider
@Produces("application/xml")
public class JAXBMarshaller<T> implements MessageBodyWriter<T> {

    private final Config config;

    public JAXBMarshaller() {
        this.config = ServiceLocator.getInstance().lookup(ConfigService.class).detach();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.isAnnotationPresent(XmlRootElement.class);
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        final String customizedNamespace = config.get(ConfigParameters.API_NAMESPACE);
        XMLStreamWriter xmlWriter = null;
        try {
            JAXBContext context = JAXBContextFactory.newInstance(type);
            Marshaller m = context.createMarshaller();
            xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(entityStream);
            XMLStreamWriter writer = new DelegatingXMLStreamWriter(xmlWriter) {
                @Override
                public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
                    super.writeStartElement(customizedNamespace, localName);
                }

                @Override
                public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
                    super.writeStartElement("", localName, customizedNamespace);
                }

                @Override
                public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
                    super.writeEmptyElement(customizedNamespace, localName);
                }

                @Override
                public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
                    super.writeEmptyElement("", localName, customizedNamespace);
                }

                @Override
                public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
                    super.writeAttribute("", customizedNamespace, localName, value);
                }

                @Override
                public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
                    super.writeAttribute(customizedNamespace, localName, value);
                }

                @Override
                public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
                    super.writeNamespace("", customizedNamespace);
                }

                @Override
                public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
                    super.writeDefaultNamespace(customizedNamespace);
                }

                @Override
                public void writeCharacters(String text) throws XMLStreamException {
                    super.writeCharacters(StringUtil.prepareForXML(text));
                }
            };
            m.marshal(t, writer);
        } catch (XMLStreamException | JAXBException e) {
            throw new WebApplicationException(e);
        } finally {
            if (xmlWriter != null) {
                try {
                    xmlWriter.close();
                } catch (XMLStreamException e) {
                    // ignore
                }
            }
        }
    }
}
