package com.foros.framework;

import org.apache.commons.lang.RandomStringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class PWSHelper {
    public static final String TOKEN_KEY = "PWSToken";

    public static void saveToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = generatePWSToken();
        session.setAttribute(TOKEN_KEY, token);
    }

    private static String generatePWSToken() {
        return RandomStringUtils.randomAlphanumeric(16);
    }

    public static String getSessionToken(HttpSession session) {
        return (String) session.getAttribute(TOKEN_KEY);
    }

    private static boolean isValidToken(HttpServletRequest request) {
        String saved = getSessionToken(request.getSession());

        if (saved == null) {
            return false;
        }

        return saved.equals(request.getParameter(TOKEN_KEY));
    }

    public static void checkToken(HttpServletRequest request) {
        if (!isValidToken(request)) {
            throw new SecurityException(request.getRequestURI() + ": Invalid PWS Token");
        }
    }

    public static void checkCSRFConstraint(HttpServletRequest request, boolean isReadOnly) {
        if (!isReadOnly) {
            String method = request.getMethod();
            if ("POST".equals(method)) {
                checkToken(request);
            } else {
                throw new SecurityException(request.getRequestURI() + ": Incorrect HTTP method " + method + ", expected = POST ");
            }
        }
    }
}
