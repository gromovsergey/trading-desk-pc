package com.foros.rs.provider;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class NamespaceFilter extends XMLFilterImpl implements ValidationEventHandler {
    private final String source;
    private final String target;
    private final ErrorHandlingFilter parent;

    public NamespaceFilter(ErrorHandlingFilter parent, String source, String target) {
        super(parent);
        this.parent = parent;
        this.source = source;
        this.target = target;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, fixUri(uri));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(fixUri(uri), localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(fixUri(uri), localName, qName);
    }

    private String fixUri(String uri) {
        if (source.equals(uri)) {
            return target;
        } else {
            return uri;
        }
    }

    @Override
    public boolean handleEvent(final ValidationEvent event) {
        return parent.handleEvent(new ValidationEvent() {
            @Override
            public int getSeverity() {
                return event.getSeverity();
            }

            @Override
            public String getMessage() {
                if (event.getMessage() == null) {
                    return null;
                }
                return event.getMessage().replace(target, source);
            }

            @Override
            public Throwable getLinkedException() {
                return event.getLinkedException();
            }

            @Override
            public ValidationEventLocator getLocator() {
                return event.getLocator();
            }
        });
    }
}
