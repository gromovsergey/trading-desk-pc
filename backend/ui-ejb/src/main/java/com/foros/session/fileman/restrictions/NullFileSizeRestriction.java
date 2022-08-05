package com.foros.session.fileman.restrictions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;

public class NullFileSizeRestriction implements FileSizeRestriction {
    public static final FileSizeRestriction INSTANCE = new NullFileSizeRestriction();

    public OutputStream wrap(OutputStream os, Quota quota, File file) throws IOException {
        return os;
    }
}
