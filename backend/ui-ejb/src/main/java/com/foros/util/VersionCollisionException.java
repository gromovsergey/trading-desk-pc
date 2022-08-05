package com.foros.util;

import javax.ejb.ApplicationException;

/**
 *
 * @author vladimir
 * @version $Revision: 1.1 $
 */
@ApplicationException(rollback = true)
public class VersionCollisionException extends RuntimeException {
    public VersionCollisionException() {}

    public VersionCollisionException(String message) {
        super(message);
    }

    public VersionCollisionException(Throwable e) {
        super(e);
    }
}
