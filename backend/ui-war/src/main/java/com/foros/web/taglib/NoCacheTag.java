package com.foros.web.taglib;

import com.foros.util.web.ResponseCacheHelper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class NoCacheTag extends TagSupport {

    @Override
    public int doStartTag() throws JspException {
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        ResponseCacheHelper.setNoCache(response);
        return SKIP_BODY;
    }

}
