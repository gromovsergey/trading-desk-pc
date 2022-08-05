package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.BadFolderNameException;

public class NullFileNameRestriction implements FileNameRestriction {
    @Override
    public void checkFileName(String folderName, String fileName, boolean isBlankAllowed) throws BadFileNameException, BadFolderNameException {
    }

    @Override
    public void checkFileName(String fileName) throws BadFileNameException, BadFolderNameException {
    }

    @Override
    public void checkFolderName(String parentDir, String folderName, boolean isBlankAllowed) throws BadFolderNameException {
    }

    @Override
    public void checkFolderName(String folderName) throws BadFolderNameException {
    }
}
