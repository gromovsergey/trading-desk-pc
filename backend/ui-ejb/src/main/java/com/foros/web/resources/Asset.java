package com.foros.web.resources;

import java.io.IOException;
import java.io.InputStream;

public interface Asset {

    String getName();

    String getContentType();

    long getVersion();

    InputStream getStream() throws IOException;

}
