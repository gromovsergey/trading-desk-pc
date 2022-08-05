package com.foros.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class ResolveGlobalTag extends TagSupport {
    private String resource;
    private String id;

    private Boolean prepare = false;

    /**
     * Method called at start of tag.
     * @return SKIP_BODY
     */
    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();

            String message = MessageResolver.resolveGlobal(resource, id, prepare);

            out.print(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SKIP_BODY;
    }

    /**
     * Method called at end of tag.
     * @return EVAL_PAGE
     */
    @Override
    public int doEndTag() {
        return EVAL_PAGE;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPrepare() {
        return prepare;
    }

    public void setPrepare(Boolean prepare) {
        this.prepare = prepare;
    }
}
