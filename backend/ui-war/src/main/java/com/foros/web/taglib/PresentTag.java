package com.foros.web.taglib;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Some analogue to Struts1 logic:present tag for using in Struts2 environment
 * @author alexey_koloskov
 */
public class PresentTag extends TagSupport {
    private String value;

    public int doStartTag() throws JspException {
        ValueStack vs = ActionContext.getContext().getValueStack();
        try {
            Object o = vs.findValue(value);
            return EVAL_BODY_INCLUDE;
        } catch (Exception e) {
            return SKIP_BODY;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
