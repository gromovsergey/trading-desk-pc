package com.foros.rs.provider;

import com.foros.util.StringUtil;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

public class TrimmingFilter extends XMLFilterImpl {

    private static final int MAX_BUFFER_SIZE = 100000;

    private StringBuilder chars = new StringBuilder();

    public TrimmingFilter(XMLReader parent) {
        super(parent);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        trimChars();
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        trimChars();
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (chars.length() + length > MAX_BUFFER_SIZE) {
            throw new SAXException("Too many chars");
        }
        chars.append(ch, start, length);
    }

    private void trimChars() throws SAXException {
        if (chars.length() != 0) {
            String trimmed = StringUtil.trimProperty(chars.toString());
            super.characters(trimmed.toCharArray(), 0, trimmed.length());
            chars.setLength(0);
        }
    }
}
