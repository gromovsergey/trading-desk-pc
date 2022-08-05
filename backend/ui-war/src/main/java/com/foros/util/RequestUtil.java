package com.foros.util;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {
    public static String getCustomizationName(HttpServletRequest request) {
        return System.getProperty("customization_" + request.getServerName() + "_" + getLocalPort(request));
    }

    public static String getBaseUrl(HttpServletRequest request) {
        String protocol = "https";
        try {
            protocol = new URL(request.getRequestURL().toString()).getProtocol();
        } catch (MalformedURLException e) {

        }

        return protocol + "://" + request.getHeader("host");
    }

    private static int getLocalPort(HttpServletRequest request) {
        return request.getLocalPort();
    }
}
