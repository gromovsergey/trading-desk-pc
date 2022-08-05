package com.foros.changes;

import javax.ejb.ApplicationException;

@ApplicationException
public class CapturingNotInitializedException extends RuntimeException {

    public CapturingNotInitializedException() {
        super("Can't register handler without changing capturing initialization! " +
                "Use CaptureChangesInterceptor or ChangesService.initialize() directly!");
    }
}
