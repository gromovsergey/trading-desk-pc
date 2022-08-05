package com.foros.session.fileman.restrictions;

import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class NullZipRestriction implements ZipRestriction {
    public static final ZipRestriction INSTANCE = new NullZipRestriction();

    public ZipInputStream create(InputStream stream) {
        return new ZipInputStream(stream);
    }
}
