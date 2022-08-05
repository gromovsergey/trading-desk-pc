package com.foros.web.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetImpl extends AbstractAsset {

    private File file;

    public AssetImpl(String name, long version, String contentType, File file) {
        super(name, version, contentType);
        this.file = file;
    }

    @Override
    public InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

}
