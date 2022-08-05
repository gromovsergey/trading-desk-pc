package com.foros.client.generator.schema
import com.foros.client.generator.utils.ResourceUtils
import org.apache.commons.io.IOUtils
import org.w3c.dom.ls.LSInput
import org.w3c.dom.ls.LSResourceResolver

import javax.xml.XMLConstants
import javax.xml.transform.Source
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory

class SchemaAggregator {
    private static final String PARENT_DIR = "xsd";
    private static final String MODEL_PATH = "xsd/model.xsd";
    private static final String GENERATOR_PATH = "xsd/generator.xsd";

    public Schema readSchema() {
        String[] sources = findSources()
        InputStream[] streams = new InputStream[sources.length]
        for (int i = 0; i < sources.length; i++) {
            streams[i] = ResourceUtils.getResource(sources[i])
        }
        return new SchemaParser().parse(streams)
    }

    public javax.xml.validation.Schema readJavaxSchema() {
        InputStream modelIs = null;
        InputStream generatorIs = null;
        try {
            modelIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(MODEL_PATH);
            generatorIs = Thread.currentThread().getContextClassLoader().getResourceAsStream(GENERATOR_PATH);
            Source modelSource = new StreamSource(modelIs);
            Source generatorSource = new StreamSource(generatorIs);
            Source[] sources = [ generatorSource, modelSource ];

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setResourceResolver(new ResourceResolver());
            return schemaFactory.newSchema(sources);
        } finally {
            IOUtils.closeQuietly(modelIs);
            IOUtils.closeQuietly(generatorIs);
        }
    }

    private String[] findSources() {
        InputStream modelIs = null;
        InputStream generatorIs = null;
        try {
            modelIs = ResourceUtils.getResource(MODEL_PATH);
            generatorIs = ResourceUtils.getResource(GENERATOR_PATH);
            Source modelSource = new StreamSource(modelIs);
            Source generatorSource = new StreamSource(generatorIs);
            Source[] sources = [ generatorSource, modelSource ];

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ResourceResolver resolver = new ResourceResolver();
            schemaFactory.setResourceResolver(resolver);
            schemaFactory.newSchema(sources);
            Set<String> result = resolver.getModelSources();
            result.add(MODEL_PATH);
            return result.toArray();
        } finally {
            IOUtils.closeQuietly(modelIs);
            IOUtils.closeQuietly(generatorIs);
        }
    }

    public class ResourceResolver implements LSResourceResolver {
        private Set<String> modelSources = new HashSet();

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
            if (systemId == null) {
                return null;
            }
            String source = PARENT_DIR + "/" + systemId;
            if (!source.equals(GENERATOR_PATH)) {
                modelSources.add(source);
            }

            InputStream resourceAsStream = ResourceUtils.getResource(source);
            return new SchemaInput(publicId, systemId, resourceAsStream);
        }

        public Set<String> getModelSources() {
            return modelSources;
        }
    }

    public class SchemaInput implements LSInput {
        private String publicId;
        private String systemId;
        private BufferedInputStream inputStream;
        private String contents;

        public SchemaInput(String publicId, String sysId, InputStream input) {
            this.publicId = publicId;
            this.systemId = sysId;
            this.inputStream = new BufferedInputStream(input);
        }

        public String getPublicId() {
            return publicId;
        }

        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        public String getBaseURI() {
            return null;
        }

        public InputStream getByteStream() {
            return null;
        }

        public boolean getCertifiedText() {
            return false;
        }

        public Reader getCharacterStream() {
            return null;
        }

        public String getEncoding() {
            return null;
        }

        public String getStringData() {
            if (contents == null) {
                contents = readStream();
            }
            return contents;
        }

        public void setBaseURI(String baseURI) {
        }

        public void setByteStream(InputStream byteStream) {
        }

        public void setCertifiedText(boolean certifiedText) {
        }

        public void setCharacterStream(Reader characterStream) {
        }

        public void setEncoding(String encoding) {
        }

        public void setStringData(String stringData) {
        }

        public String getSystemId() {
            return systemId;
        }

        public void setSystemId(String systemId) {
            this.systemId = systemId;
        }

        public BufferedInputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(BufferedInputStream inputStream) {
            this.inputStream = inputStream;
        }

        private String readStream() {
            try {
                byte[] input = new byte[inputStream.available()];
                inputStream.read(input);
                return new String(input);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

}
