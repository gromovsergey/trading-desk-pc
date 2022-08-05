package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.BadFolderNameException;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.PathProvider;
import com.foros.util.StringUtil;

import java.util.Scanner;

public class FileNameRestrictionImpl implements FileNameRestriction{
    public static final FileNameRestriction INSTANCE = new FileNameRestrictionImpl();

    private enum FileFolder {
        FILE(),
        FOLDER();
    }

    @Override
    public void checkFileName(String fileName) throws BadFileNameException, BadFolderNameException {
        checkFileName("", fileName, true);
    }

    @Override
    public void checkFileName(String folderName, String fileName, boolean isBlankAllowed) throws BadFileNameException, BadFolderNameException {
        if (StringUtil.isPropertyNotEmpty(folderName)) checkFolderName(folderName, true);
        checkName(fileName, isBlankAllowed, FileFolder.FILE);
    }

    @Override
    public void checkFolderName(String folderName) throws BadFolderNameException {
        checkFolderName("", folderName, true);
    }

    @Override
    public void checkFolderName(String parentDir, String folderName, boolean isBlankAllowed) throws BadFolderNameException {
        if (StringUtil.isPropertyNotEmpty(parentDir)) {
            checkFolderName(parentDir, true);
        }
        checkFolderName(folderName, isBlankAllowed);
    }

    private void checkFolderName(String folderName, boolean isBlankAllowed) {
        checkName(folderName, isBlankAllowed, FileFolder.FOLDER);
    }

    private void checkName(String fileOrFolderName, boolean isBlankAllowed, FileFolder fileFolder) {
        fileOrFolderName = FileUtils.extractPathName(fileOrFolderName);
        if (StringUtil.isPropertyEmpty(fileOrFolderName)) {
            if (isBlankAllowed) {
                return;
            }

            if (fileFolder.equals(FileFolder.FILE)) {
                throw new BadFileNameException(fileOrFolderName);
            }

            throw new BadFolderNameException(fileOrFolderName);
        }

        try (Scanner scanner = new Scanner(fileOrFolderName)) {
            scanner.useDelimiter(PathProvider.PATH_SEPARATOR);
            while (scanner.hasNext()) {
                String name = scanner.next();
                if (!FileUtils.isNamespaceRestrictionsApply(name)) {
                    if (fileFolder.equals(FileFolder.FILE) && !scanner.hasNext()) {
                        throw new BadFileNameException(fileOrFolderName);
                    }
                    throw new BadFolderNameException(fileOrFolderName);
                }
            }
        }
    }
}
