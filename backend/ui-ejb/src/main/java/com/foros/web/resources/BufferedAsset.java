package com.foros.web.resources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BufferedAsset extends AbstractAsset {

    private byte[] buffer;

    public BufferedAsset(String name, long version, String contentType, byte[] buffer) {
        super(name, version, contentType);
        this.buffer = buffer;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(buffer);
    }

}
