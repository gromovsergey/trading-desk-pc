package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.PathProvider;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.regex.Pattern;

public class TextAdImageRestrictionFilter implements RestrictionFilter {
    private String imagesFolderName;
    private String[] creativeFolderPath;
    private PathProvider creativesPathProvider;

    public TextAdImageRestrictionFilter(String imagesFolderName, PathProvider creativesPathProvider) {
        this.imagesFolderName = imagesFolderName;
        this.creativesPathProvider = creativesPathProvider;
        this.creativeFolderPath = creativesPathProvider.getPath("").getPath().split(Pattern.quote(File.separator));
    }

    @Override
    public boolean accept(File file) {
        String[] that = file.getPath().split(Pattern.quote(File.separator));

        if (that.length <= creativeFolderPath.length + 2) {
            return false;
        }

        for (int i = 0 ; i < creativeFolderPath.length; i++) {
            if (!FilenameUtils.equalsOnSystem(that[i], creativeFolderPath[i])) {
                return false;
            }
        }

        File path;

        // first level is account folder
        String first = that[creativeFolderPath.length];
        // it should be positive long value
        if (!first.matches("\\d+")) {
            return false;
        }
        FileNameRestrictionImpl.INSTANCE.checkFolderName(first);

        // second level is account folder or text ad image folder
        String second = that[creativeFolderPath.length + 1];
        if (second.matches("\\d+")) {
            FileNameRestrictionImpl.INSTANCE.checkFolderName(second);

            String third = that[creativeFolderPath.length + 2];
            if (!FilenameUtils.equalsOnSystem(third, imagesFolderName)) {
                return false;
            }
            path = creativesPathProvider.getPath(first + "/" + second + "/" + third);
        } else if (FilenameUtils.equalsOnSystem(second, imagesFolderName)) {
            path = creativesPathProvider.getPath(first + "/" + second);
        } else {
            return false;
        }

        if (!path.exists()) {
            return false;
        }

        return true;
    }
}
