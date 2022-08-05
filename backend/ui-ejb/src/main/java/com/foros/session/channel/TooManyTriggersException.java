package com.foros.session.channel;

import com.foros.session.TooManyRowsException;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class TooManyTriggersException extends TooManyRowsException {
    private int maxTriggersCount;

    public TooManyTriggersException(int maxTriggersCount) {
        this.maxTriggersCount = maxTriggersCount;
    }

    public int getMaxTriggersCount() {
        return maxTriggersCount;
    }
}
