package com.foros.rs.client;

import com.foros.rs.client.data.JAXBResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ByteArrayEntity;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

public class ValidatingJAXBResponseHandler extends JAXBResponseHandler {
    private static final String PARENT_DIR = "xsd";
    private static final String MODEL_PATH = "xsd/model.xsd";
    private static final String GENERATOR_PATH = "xsd/generator.xsd";

    private Schema schema;

    public ValidatingJAXBResponseHandler() throws SAXException {
        InputStream modelIs = null;
        InputStream generatorIs = null;
        try {
            modelIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(MODEL_PATH);
            generatorIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(GENERATOR_PATH);
            Source modelSource = new StreamSource(modelIs);
            Source generatorSource = new StreamSource(generatorIs);
            Source[] sources = { generatorSource, modelSource };

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new ResourceResolver());
            schema = schemaFactory.newSchema(sources);
        } finally {
            IOUtils.closeQuietly(modelIs);
            IOUtils.closeQuietly(generatorIs);
        }
    }

    @Override
    protected Unmarshaller createUnmarshaller(JAXBContext jaxbContext) throws JAXBException {
        Unmarshaller unmarshaller = super.createUnmarshaller(jaxbContext);
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }

    @Override
    public Object handleResponse(HttpResponse response) throws IOException {
        Object res;
        byte[] buf = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            IOUtils.copy(response.getEntity().getContent(), baos);
            buf = baos.toByteArray();
            response.setEntity(new ByteArrayEntity(buf));
            res = super.handleResponse(response);
            buf = null;
        } finally {
            if (buf != null) {
                System.out.write(buf);
            }
        }
        return res;
    }

    public class ResourceResolver implements LSResourceResolver {
        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            if (systemId == null) {
                return null;
            }
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream resourceAsStream = classLoader.getResourceAsStream(PARENT_DIR + "/" + systemId);
            return new SchemaInput(publicId, systemId, resourceAsStream);
        }
    }
}
