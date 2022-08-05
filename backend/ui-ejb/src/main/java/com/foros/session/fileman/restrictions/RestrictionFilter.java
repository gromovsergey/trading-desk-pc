package com.foros.session.fileman.restrictions;

import java.io.File;
import java.io.IOException;

public interface RestrictionFilter {
    boolean accept(File file) throws IOException;
}
