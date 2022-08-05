package com.foros.session.fileman.restrictions;

import java.io.File;

public class NullFileContentRestriction implements FileContentRestriction {
    public static final FileContentRestriction INSTANCE = new NullFileContentRestriction();

    public void check(File file) {
    }
}
