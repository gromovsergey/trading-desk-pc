package com.foros.session.fileman.restrictions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class TextAdImageFileSizeRestriction extends FileSizeRestrictionImpl {
    private int defaultMaxSize;
    private int textAdMaxSize;

    private RestrictionFilter filter;

    public TextAdImageFileSizeRestriction(int defaultMaxSize, int textAdMaxSize, RestrictionFilter filter) {
        this.defaultMaxSize = defaultMaxSize;
        this.textAdMaxSize = textAdMaxSize;
        this.filter = filter;
    }

    @Override
    public OutputStream wrap(OutputStream os, Quota quota, final File file) throws IOException {
        if (filter.accept(file)) {
            maxFileSize = textAdMaxSize;
        } else {
            maxFileSize = defaultMaxSize;
        }

        return super.wrap(os, quota, file);
    }
}

