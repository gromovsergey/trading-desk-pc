package com.foros.rs.client.data;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayContentSource extends ContentSource {
    private final byte[] content;

    public ByteArrayContentSource(byte[] buf) {
        this.content = buf;
    }

    @Override
    public long getLength() {
        return (long) content.length;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(content);
    }

    public byte[] getContent() {
        return content;
    }
}
