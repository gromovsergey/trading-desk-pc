package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.FileContentException;
import com.foros.session.fileman.FileTypesRestrictionImpl;
import com.foros.session.fileman.ImageDimensionException;
import com.foros.util.ImageUtil;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class TextAdImageFileContentRestriction implements FileContentRestriction {
    private RestrictionFilter filter;

    private int maxDimension;
    private List<String> allowedFileTypes;

    public TextAdImageFileContentRestriction(int maxDimension, List<String> allowedFileTypes, RestrictionFilter filter) {
        this.maxDimension = maxDimension;
        this.allowedFileTypes = allowedFileTypes;
        this.filter = filter;
    }

    @Override
    public void check(File file) throws IOException {
        if (filter.accept(file)) {
            doCheck(file);
        }
    }

    private void doCheck(File file) throws IOException {
        if (!FileTypesRestrictionImpl.INSTANCE.check(file, allowedFileTypes)) {
            FileContentException ex = new FileContentException(file.getName(), file.getPath());
            ex.setExtensionCorrespondsContent(false);
            throw ex;
        }

        Dimension dimension = ImageUtil.getImageDimensions(file);
        if (dimension != null && (dimension.getHeight() > maxDimension || dimension.getWidth() > maxDimension)) {
            throw new ImageDimensionException(file.getName(), file.getPath(), maxDimension);
        }
    }
}