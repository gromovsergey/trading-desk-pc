package com.foros.util;

import com.opensymphony.xwork2.interceptor.I18nInterceptor;
import com.foros.model.security.Language;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;

public class LocaleHelper {
    private static final String LOCALE_KEY = I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE;

    public static void setLocale(Locale locale, HttpServletRequest request) {
        // set up locale for Struts 2.
        request.getSession().setAttribute(LOCALE_KEY, locale);
        // set up locale for JSTL
        Config.set(request.getSession(), Config.FMT_LOCALE, locale);
    }

    public static Locale getLocale(HttpServletRequest request) {
        return (Locale) request.getAttribute(LOCALE_KEY);
    }

    public static String getLanguageString(HttpServletRequest request) {
        Locale locale = (Locale)request.getSession().getAttribute(LOCALE_KEY);

        if (locale != null) {
            return locale.getLanguage();
        }

        return Language.EN.getIsoCode();
    }

}
