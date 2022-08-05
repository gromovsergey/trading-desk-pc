package com.foros.util.xml;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.StringReader;
import java.io.IOException;

/**
 * @author dmitry_antonov
 * @since 19.03.2008
 */
public class XmlHelper {
    protected XmlHelper() {
    }

    /**
     * Parses string xml as w3c Document.
     * @param content text-represented xml.
     * @return parsed Document if content is valid or null otherwise.
     */
    public static Document parse(String content) {
        try {
            return internalParse(new InputSource(new StringReader(content)));
        } catch (Exception e) {
            return null;
        }
    }

    protected static Document internalParse(InputSource source) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        
        return documentBuilder.parse(source);
    }
}
