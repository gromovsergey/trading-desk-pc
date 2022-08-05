package com.foros.session.fileman.restrictions;

import java.util.zip.ZipInputStream;
import java.io.InputStream;

public interface ZipRestriction {
    ZipInputStream create(InputStream stream);
}