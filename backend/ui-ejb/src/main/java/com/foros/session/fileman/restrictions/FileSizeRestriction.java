package com.foros.session.fileman.restrictions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public interface FileSizeRestriction {
    OutputStream wrap(OutputStream os, Quota quota, File file) throws IOException;
}
