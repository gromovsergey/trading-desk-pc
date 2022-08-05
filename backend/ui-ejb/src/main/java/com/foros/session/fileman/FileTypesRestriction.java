package com.foros.session.fileman;

import java.io.File;
import java.util.List;

public interface FileTypesRestriction {
    boolean check(File file, List<String> allowedFileTypes);
}
