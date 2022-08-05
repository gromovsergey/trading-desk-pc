package com.foros.audit.serialize.serializer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

public class DomHelper {

    public static String documentToString(Document document) {
        try {
            StringWriter documentWriter = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(documentWriter, format);
            writer.write(document);
            return documentWriter.toString();
        } catch (IOException e) {
            throw new RuntimeException("Can't serialize xml!", e);
        }
    }

    public static Document stringToDocument(String str) {
        try {
            return DocumentHelper.parseText(str);
        } catch (DocumentException e) {
            throw new RuntimeException("Can't parse xml!", e);
        }
    }

    public static String getXPathString(Node node, String path, Map<String, String> namespaceUris) {
        XPath xPath = DocumentHelper.createXPath(path);
        xPath.setNamespaceURIs(namespaceUris);
        Node resNode = xPath.selectSingleNode(node);
        return resNode != null ? resNode.getText() : null;
    }

    public static List<Node> getXPathNodes(Node node, String path, Map<String, String> namespaceUris) {
        XPath xPath = DocumentHelper.createXPath(path);
        xPath.setNamespaceURIs(namespaceUris);
        return xPath.selectNodes(node);
    }
}
