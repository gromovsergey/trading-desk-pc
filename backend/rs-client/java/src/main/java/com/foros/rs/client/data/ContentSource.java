package com.foros.rs.client.data;

import com.foros.rs.client.MimeType;

import java.io.IOException;
import java.io.OutputStream;

public abstract class ContentSource {
    public long getLength() {
        return -1;
    }

    public abstract void writeTo(OutputStream os) throws IOException;

    public MimeType getContentType() {
        return null;
    }
}
