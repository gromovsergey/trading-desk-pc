package com.foros.util;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.springframework.util.Assert;

public class CookiesContainer {

    private Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    public CookiesContainer(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.getName(), cookie);
            }
        }
    }

    public Long getLong(String name) {
        try {
            return Long.parseLong(get(name));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String get(String name) {
        Cookie cookie = cookies.get(name);

        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            return cookie.getValue();
        }

        return null;
    }
}
