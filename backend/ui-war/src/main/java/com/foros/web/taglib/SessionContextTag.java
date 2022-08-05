package com.foros.web.taglib;

import com.foros.util.context.SessionContexts;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.rt.core.SetTag;

public class SessionContextTag extends SetTag {

    @Override
    public int doStartTag() throws JspException {
        setScope("page");
        setValue(SessionContexts.getSessionContexts((HttpServletRequest) pageContext.getRequest()));
        return super.doStartTag();
    }
}
