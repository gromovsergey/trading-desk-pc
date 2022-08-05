package com.foros.ui.authentication.web.taglib;

import javax.servlet.http.HttpServletRequest;

public class Functions {
    private static String MOBILE_AGENT_PATTERN = "(?s).*(\\bAndroid\\b|\\bwebOS\\b|\\biPhone\\b|\\biPad\\b|\\biPod\\b|\\bBlackBerry\\b|\\bIEMobile\\b|\\b(Opera Mini)\\b).*";

    public static boolean isMobileAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && userAgent.matches(MOBILE_AGENT_PATTERN);
    }

}
