package com.foros.session.channel.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TriggerTooLargeException extends RuntimeException {

    public TriggerTooLargeException(String message) {
        super(message);
    }

}
