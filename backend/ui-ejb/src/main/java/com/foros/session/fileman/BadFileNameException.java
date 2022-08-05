package com.foros.session.fileman;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class BadFileNameException extends BadNameException {
    private String fileName;

    public BadFileNameException(String fileName) {
        super("Wrong file name '" + fileName + "'");
        
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
