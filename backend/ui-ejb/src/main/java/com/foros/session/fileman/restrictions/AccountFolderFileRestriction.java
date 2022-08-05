package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.FileManagerException;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class AccountFolderFileRestriction implements FileRestriction {
    private FileRestriction target;
    private String[] creativeFolderPath;
    private PathProvider creativePathProvider;

    public AccountFolderFileRestriction(PathProvider creativePathProvider, FileRestriction target) {
        this.target = target;
        this.creativePathProvider = creativePathProvider;
        this.creativeFolderPath = creativePathProvider.getPath("").getPath().split(Pattern.quote(File.separator));
    }

    @Override
    public void checkNewFolderAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        PathProvider accountPathProvider = findAccount(file);
        if (accountPathProvider != null) {
            target.checkNewFolderAllowed(quota, accountPathProvider, file);
        }
    }

    @Override
    public void checkNewFileAllowed(Quota quota, PathProvider pathProvider, File file) throws IOException {
        PathProvider accountPathProvider = findAccount(file);
        if (accountPathProvider != null) {
            target.checkNewFileAllowed(quota, accountPathProvider, file);
        }
    }

    private PathProvider findAccount(File file) throws IOException {
        String[] that = file.getPath().split(Pattern.quote(File.separator));

        // is deeper?
        if (that.length <= creativeFolderPath.length + 1) {
            return null;
        }

        for ( int i = 0 ; i < creativeFolderPath.length; i++) {
            if(!FilenameUtils.equalsOnSystem(that[i], creativeFolderPath[i])) {
                return null;
            }
        }

        // text one is account folder
        String accountFolderName = that[creativeFolderPath.length];

        // it should be positive long value
        if (!accountFolderName.matches("\\d+")) {
            return null;
        }

        FileNameRestrictionImpl.INSTANCE.checkFolderName(accountFolderName);
        File path = creativePathProvider.getPath(accountFolderName);
        if (path.exists()) {
            // it should be directory
            if (!path.isDirectory()) {
                return null;
            }
        } else {
            if (that.length > (creativeFolderPath.length + 1)) {
                throw new FileManagerException("Create handle uncreated account folder");
            }
        }

        return PathProviderUtil.getNested(creativePathProvider, accountFolderName);
    }
}
