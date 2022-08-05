package com.foros.util.xml;

import com.foros.util.resource.ResourceHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author oleg_roshka
 */
public class WarXmlHelper extends XmlHelper {
    private WarXmlHelper() {
    }

    public static Document loadDocumentFromResource(String resourcePath)
            throws ParserConfigurationException, SAXException, IOException {
        InputStream stream = ResourceHelper.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new FileNotFoundException(resourcePath);
        }
        
        return internalParse(new InputSource(stream));
    }
}
