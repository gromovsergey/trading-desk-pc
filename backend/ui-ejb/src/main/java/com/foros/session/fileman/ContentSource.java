package com.foros.session.fileman;

import java.io.InputStream;
import java.io.IOException;

public interface ContentSource {
    long getLength();

    String getName();

    InputStream getStream() throws IOException;
}
