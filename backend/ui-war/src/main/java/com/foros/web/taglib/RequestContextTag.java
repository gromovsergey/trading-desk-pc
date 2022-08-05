package com.foros.web.taglib;

import com.foros.action.account.ContextNotSetException;
import com.foros.util.context.RequestContexts;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.rt.core.SetTag;

public class RequestContextTag extends SetTag {

    @Override
    public int doStartTag() throws JspException {
        setScope("page");
        RequestContexts requestContexts = RequestContexts.getRequestContexts((HttpServletRequest) pageContext.getRequest());
        if (requestContexts.getAdvertiserContext().isSet()) {
            setValue(requestContexts.getAdvertiserContext());
        } else if (requestContexts.getPublisherContext().isSet()) {
            setValue(requestContexts.getPublisherContext());
        } else if (requestContexts.getIspContext().isSet()) {
            setValue(requestContexts.getIspContext());
        } else if (requestContexts.getCmpContext().isSet()) {
                setValue(requestContexts.getCmpContext());
        } else {
            throw new ContextNotSetException("error.context.notset");
        }
        return super.doStartTag();
    }
}
