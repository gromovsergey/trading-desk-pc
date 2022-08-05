package com.foros.rs.client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.ls.LSInput;

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
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
