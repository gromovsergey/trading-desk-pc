package com.foros.profiling.request;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static ThreadLocal<HttpServletRequest> requestInfo = new ThreadLocal<HttpServletRequest>();

    public static HttpServletRequest get() {
        return requestInfo.get();
    }

    public static void set(HttpServletRequest request) {
        requestInfo.set(request);
    }

}
