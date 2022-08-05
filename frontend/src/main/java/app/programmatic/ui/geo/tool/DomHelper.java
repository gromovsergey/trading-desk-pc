package app.programmatic.ui.geo.tool;

import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;

import java.util.List;
import java.util.Map;

public class DomHelper {

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
