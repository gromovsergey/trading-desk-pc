package com.foros.util;

import com.google.code.kaptcha.Constants;
import javax.servlet.http.HttpSession;

public class KaptchaUtils {
    private KaptchaUtils() {
    }

    public static String read(HttpSession session) {
        String val = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        return val;
    }
}
