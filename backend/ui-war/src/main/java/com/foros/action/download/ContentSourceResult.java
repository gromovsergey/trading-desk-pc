package com.foros.action.download;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContentSourceResult extends AbstractContentSourceResult {
    protected void initResponse(HttpServletRequest request, HttpServletResponse response) {
        if (getContentType().startsWith("text/")) {
            response.setCharacterEncoding("UTF-8");
        }
        // Set file related headers
        response.setHeader("Content-Disposition", "inline; filename=\"" + getTargetFile() + "\"");
    }
}
