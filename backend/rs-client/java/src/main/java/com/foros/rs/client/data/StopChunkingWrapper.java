package com.foros.rs.client.data;

import com.foros.rs.client.RsException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

// Glassfish goes mad if request is chunked and ajp is used so we have to do this nasty thing.
public class StopChunkingWrapper implements HttpEntity {
    private final HttpEntity wrapped;
    private byte[] buffered;

    public StopChunkingWrapper(HttpEntity wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public long getContentLength() {
        return ensureBuffered().length;
    }

    @Override
    public Header getContentType() {
        return wrapped.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return wrapped.getContentEncoding();
    }

    @Override
    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(ensureBuffered());
    }


    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(ensureBuffered());
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
    public boolean isStreaming() {
        return false;
    }

    private byte[] ensureBuffered() {
        if (buffered == null) {
            try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                wrapped.writeTo(baos);
                buffered = baos.toByteArray();
            } catch (IOException e) {
                throw new RsException(e);
            }
        }
        return buffered;
    }


    @Override
    public void consumeContent() throws IOException {
    }

    public static HttpEntity wrap(HttpEntity entity) {
        if (entity == null) {
            return null;
        }

        if (entity.isChunked() || entity.getContentLength() < 0) {
            return new StopChunkingWrapper(entity);
        }

        return entity;
    }
}
