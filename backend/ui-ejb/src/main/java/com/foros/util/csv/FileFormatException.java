package com.foros.util.csv;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class FileFormatException extends RuntimeException {

    public FileFormatException(String string) {
        super(string);
    }

}
