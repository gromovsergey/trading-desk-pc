package com.foros.security.spring;

import com.foros.util.CookiesContainer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class LastSwitchedUserHelper {

    private static String LAST_LOGGED_USER_KEY = "_last_logged_user";

    public static Long getLastSwitchedUser(HttpServletRequest request) {
        CookiesContainer cookies = new CookiesContainer(request.getCookies());
        return cookies.getLong(LAST_LOGGED_USER_KEY);
    }

    public static void saveLastSwitchedUser(HttpServletResponse response, Long id) {
        Cookie cookie = new Cookie(LAST_LOGGED_USER_KEY, Long.toString(id));
        cookie.setPath("/");
        cookie.setMaxAge((24 * 60 * 60) * 30);
        response.addCookie(cookie);
    }

}
