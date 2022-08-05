package com.foros.web.taglib.wraper;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.components.UIBean;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.Writer;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@StrutsTag(name="wrap", tldTagClass="com.foros.web.taglib.wraper.WrapperTag", description="Wrap body HTML with template")
public class NodeListComponent extends UIBean {
    private final static String TEMPLATE = "fieldAndAccessories";
    private static Logger logger = Logger.getLogger(NodeListComponent.class.getName());

    private List<Node> nodes;

    public NodeListComponent(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public boolean usesBody() {
        return true;
    }

    @Override
    public boolean end(Writer writer, String body) {
        try {
            HtmlParser parser = new HtmlParser();
            nodes = parser.parse(body).getNodes();
            return super.end(writer, "");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Can't process the body: " + body, e);
            return super.end(writer, body);
        }
    }
}
