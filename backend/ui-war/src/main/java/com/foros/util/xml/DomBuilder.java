package com.foros.util.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.*;
import java.io.StringWriter;

/**
 * Author: Boris Vanin
 */
public class DomBuilder {

    private Document document;

    public DomBuilder(Document document) {
        this.document = document;
    }

    public Element createTextElement(String name, String text) {
        Element element = createElement(name);
        element.appendChild(document.createTextNode(text));
        return element;
    }

    public Element createElement(String name) {
        return document.createElement(name);
    }

    public Document getDocument() {
        return document;
    }

    public String asString() {
        try {
            DOMSource domSource = new DOMSource(document);
            StringWriter result = new StringWriter();
            StreamResult streamResult = new StreamResult(result);

            Transformer serializer = createTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF8");
            serializer.setOutputProperty(OutputKeys.INDENT, "no");
            serializer.transform(domSource, streamResult);

            return result.getBuffer().toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    public Element createRoot(String name) {
        Element element = createElement(name);
        document.appendChild(element);
        return element;
    }

    public static DomBuilder create() {
        javax.xml.parsers.DocumentBuilder builder = createDocumentBuilder();
        return new DomBuilder(builder.newDocument());
    }

    private Transformer createTransformer() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            return transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static javax.xml.parsers.DocumentBuilder createDocumentBuilder() {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            return documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
