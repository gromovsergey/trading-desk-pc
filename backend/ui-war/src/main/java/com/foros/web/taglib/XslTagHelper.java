package com.foros.web.taglib;

import com.foros.util.xml.ClassPathURIResolver;
import com.foros.util.xml.WarXmlHelper;
import org.w3c.dom.Document;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author oleg_roshka
 */
public class XslTagHelper {
    private static Map<String, Templates> templatesMap = new HashMap<String, Templates>();

    private XslTagHelper() {
    }

    /**
     * xsl transformation
     * @param content
     * @param xslResourcePath
     * @return
     */
    public static String transform(String content, String xslResourcePath) {
        Document doc = WarXmlHelper.parse(content);

        return transformDoc(doc, xslResourcePath);
    }

    private static synchronized Transformer getTransformer(String xslResourcePath) throws TransformerConfigurationException, FileNotFoundException {
        Templates templates = templatesMap.get(xslResourcePath);
        if (templates == null) {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            InputStream stream = contextClassLoader.getResourceAsStream(xslResourcePath);
            if (stream == null) {
                throw new FileNotFoundException(xslResourcePath);
            }

            TransformerFactory factory = TransformerFactory.newInstance();
            templates = factory.newTemplates(new StreamSource(stream));
            templatesMap.put(xslResourcePath, templates);
        }
        
        return templates.newTransformer();
    }

    private static String transformDoc(Document doc, String xslResourcePath) {
        try {
            DOMSource domSource = new DOMSource(doc);
            Transformer transformer = getTransformer(xslResourcePath);
            transformer.setURIResolver(new ClassPathURIResolver(""));
            StringWriter stringWriter = new StringWriter();

            transformer.transform(domSource, new StreamResult(stringWriter));

            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
