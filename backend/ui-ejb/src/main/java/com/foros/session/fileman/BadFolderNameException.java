package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BadFolderNameException extends BadNameException {
    private String folderName;

    public BadFolderNameException(String folderName) {
        super("Wrong folder name '" + folderName + "'");

        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}
