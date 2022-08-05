package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.BadFolderNameException;

public interface FileNameRestriction {
    public void checkFileName(String folderName, String fileName, boolean isBlankAllowed) throws BadFileNameException, BadFolderNameException;

    public void checkFileName(String fileName) throws BadFileNameException, BadFolderNameException;

    public void checkFolderName(String parentDir, String folderName, boolean isBlankAllowed) throws BadFolderNameException;

    public void checkFolderName(String folderName) throws BadFolderNameException;

}
