package com.foros.web.taglib;
import com.opensymphony.xwork2.ActionContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class SetTag extends TagSupport {
    private String name;
    private String scope = "page";
    private Object value;

    public void setName(String name) {
        this.name = name;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override    
    public int doStartTag() throws JspException {
        if ("application".equals(scope)) {
            super.pageContext.getServletContext().setAttribute(name, value);
        } else if ("session".equals(scope)) {
            pageContext.getSession().setAttribute(name, value);
        } else if ("request".equals(scope)) {
            pageContext.getRequest().setAttribute(name, value);
        } else if ("page".equals(scope)) {
            pageContext.setAttribute(name, value);
        } else {
            ActionContext.getContext().put(name, value);
        }

        return EVAL_PAGE;
    }

    /**
     * Clears all the instance variables to allow this instance to be reused.
     */
    @Override
    public void release() {
        super.release();
        this.name = null;
        this.scope = null;
        this.value = null;
    }
}
