package com.foros.session.fileman.restrictions;

import java.io.File;

public class NullRestrictionFilter implements RestrictionFilter {
    public static final RestrictionFilter INSTANCE = new NullRestrictionFilter();

    @Override
    public boolean accept(File file) {
        return true;
    }
}