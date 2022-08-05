package com.foros.web.resources;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractAsset implements Asset {
    protected String name;
    private long version;
    protected String contentType;

    public AbstractAsset(String name, long version, String contentType) {
        this.name = name;
        this.version = version;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public abstract InputStream getStream() throws IOException;

}
