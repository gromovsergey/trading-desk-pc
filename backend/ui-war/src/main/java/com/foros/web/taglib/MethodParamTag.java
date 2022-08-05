package com.foros.web.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;

public class MethodParamTag extends TagSupport {
    protected Object value;
    protected String className;

    public void setValue(Object value) {
        this.value = value;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public MethodParamTag() {
        value = null;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            ParameterizedTag parent = (ParameterizedTag) findAncestorWithClass(this, ParameterizedTag.class);

            if (parent != null) {
                parent.addParameter(value, className);
            }
        } catch (RuntimeException e) {
            throw new JspException(e);
        }

        return EVAL_PAGE;
    }

    @Override
    public void release() {
        super.release();
        value = null;
    }
}
