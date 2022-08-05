package com.foros.rs.client.data;

import com.foros.rs.client.MimeType;
import com.foros.rs.client.rsclient.data.JAXBUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class JAXBEntity implements HttpEntity {

    private final Object bean;
    private static final JAXBContext CONTEXT = JAXBUtils.getContext();

    public JAXBEntity(Object bean) {
        this.bean = bean;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type", MimeType.APPLICATION_XML.getType());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeTo(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        try {
            CONTEXT.createMarshaller().marshal(bean, os);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {
    }
}
