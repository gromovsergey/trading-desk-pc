package com.foros.rs.client.data;

import com.foros.rs.client.MimeType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class ContentSourceEntity implements HttpEntity {
    private final ContentSource source;

    public ContentSourceEntity(ContentSource source) {
        this.source = source;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        Long length = source.getLength();
        return length == null ? -1 : length;
    }

    @Override
    public Header getContentType() {
        MimeType contentType = source.getContentType();
        return contentType == null ? null : new BasicHeader("Content-Type", contentType.getType());
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return null;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        source.writeTo(os);
    }

    @Override
    public boolean isStreaming() {
        return true;
    }

    @Override
    public void consumeContent() throws IOException {
    }
}
