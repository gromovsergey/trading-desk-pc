package com.foros.session.fileman;

import java.io.File;
import java.util.List;

public class NullFileTypesRestrictionImpl implements FileTypesRestriction {
    public static final FileTypesRestriction INSTANCE = new NullFileTypesRestrictionImpl();

    @Override
    public boolean check(File file, List<String> allowedFileTypes) {
        return true;
    }

}
