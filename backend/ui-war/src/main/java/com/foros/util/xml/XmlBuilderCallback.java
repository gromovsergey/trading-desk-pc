package com.foros.util.xml;


import java.io.Writer;
import java.io.IOException;

/**
 * Basic abstract class for callback. Main responsibility it to format xml inside <code>write</code> method
 * implementation.
 *
 * @author alexey_chernenko
 */
public abstract class XmlBuilderCallback {
    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";

    /**
     * Implementation of this method is responsible to writing valid xml result into provided writer
     */
    public abstract void write(Writer writer) throws Exception;

    /**
     * Default implementation does nothing
     */
    public void handleException(Writer writer, Exception e) throws IOException {
    }
}
