package com.foros.session.fileman.restrictions;

import com.foros.session.fileman.AccountSizeExceededException;
import com.foros.session.fileman.FileSizeException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class FileSizeRestrictionImpl implements FileSizeRestriction {
    protected int maxFileSize;

    public FileSizeRestrictionImpl() {
    }

    public FileSizeRestrictionImpl(int maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /** Returns an OutputStream WRAPPED around the given OutputStream os
     *  So, on it's close invokes the wrapped os' close()
     */
    @Override
    public OutputStream wrap(OutputStream os, Quota quota, final File file) throws IOException {
        long available = quota.getFileSizesAvailable();
        if (available != Quota.NO_LIMIT) {
            long currentLength = file.length();
            available += currentLength;

            if (available < maxFileSize) {
                return new RestrictedOutputStream(os, (int) available) {
                    @Override
                    protected void thresholdReached() throws IOException {
                        throw new AccountSizeExceededException(file.getName());
                    }
                };
            }
        }

        return new RestrictedOutputStream(os, maxFileSize) {
            @Override
            protected void thresholdReached() throws IOException {
                throw new FileSizeException(file.getName(), getThreshold());
            }
        };
    }
}
