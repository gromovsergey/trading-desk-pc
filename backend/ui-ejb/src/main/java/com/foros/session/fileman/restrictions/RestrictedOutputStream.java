package com.foros.session.fileman.restrictions;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.output.ThresholdingOutputStream;

public abstract class RestrictedOutputStream extends ThresholdingOutputStream {
    private OutputStream wrapped;

    public RestrictedOutputStream(OutputStream wrapped, int threshhold) {
        super(threshhold);
        this.wrapped = wrapped;
    }

    protected OutputStream getStream() throws IOException {
        return wrapped;
    }

}
