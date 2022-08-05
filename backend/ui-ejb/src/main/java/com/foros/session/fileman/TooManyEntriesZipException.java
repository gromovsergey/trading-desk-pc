package com.foros.session.fileman;

import java.util.zip.ZipException;

/**
 * @author Vitaliy_Knyazev
 */
public class TooManyEntriesZipException extends ZipException {
    public TooManyEntriesZipException(String message) {
        super(message);
    }
}
