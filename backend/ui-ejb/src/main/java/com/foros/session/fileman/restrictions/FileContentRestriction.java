package com.foros.session.fileman.restrictions;

import java.io.File;
import java.io.IOException;

public interface FileContentRestriction {
    void check(File file) throws IOException;
}