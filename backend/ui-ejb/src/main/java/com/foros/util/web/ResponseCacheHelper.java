package com.foros.util.web;

import javax.servlet.http.HttpServletResponse;

public abstract class ResponseCacheHelper {

    private ResponseCacheHelper() {
    }

    public static void setNoCache(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); //HTTP 1.1
        response.setHeader("Pragma", "no-cache");        //HTTP 1.0
        response.setDateHeader("Expires", 0);            //prevents caching at the proxy server
    }

    public static void setCached(HttpServletResponse response, long time) {
        response.addHeader("Cache-Control", "public, max-age=" + time / 1000);
        response.addHeader("Pragma", "");
        response.addDateHeader("Expires", System.currentTimeMillis() + time);
    }
}
