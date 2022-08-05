package com.foros.security.spring.provider;

import com.foros.security.principal.Tokenable;
import com.foros.util.CookiesContainer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class TokenUtils {

    private static String TOKEN_COOKIE_NAME = "_AUTHENTICATION_TOKEN";

    public static void saveToken(HttpServletRequest request, HttpServletResponse response, Tokenable authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getToken().isEmpty()) {
            updateToken(authentication.getToken(), request, response);
        } else {
            removeToken(request, response);
        }
    }

    private static Cookie createCookie(String token, boolean secure) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        if (secure) {
            cookie.setSecure(true);
        }
        cookie.setMaxAge(-1);
        return cookie;
    }

    private static Cookie createDeleteCookie(boolean secure) {
        Cookie cookie = new Cookie(TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        if (secure) {
            cookie.setSecure(true);
        }
        cookie.setMaxAge(0);
        return cookie;
    }

    public static String fetchTokenFromRequest(HttpServletRequest request) {
        return new CookiesContainer(request.getCookies()).get(TOKEN_COOKIE_NAME);
    }

    public static void updateToken(String token, HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(createCookie(token, request.isSecure()));
    }

    public static void removeToken(HttpServletRequest request, HttpServletResponse response) {
        response.addCookie(createDeleteCookie(request.isSecure()));
    }

}
