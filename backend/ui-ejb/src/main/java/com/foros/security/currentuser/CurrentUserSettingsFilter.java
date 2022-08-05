package com.foros.security.currentuser;

import com.foros.model.security.User;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.security.UserService;
import com.foros.util.CookiesContainer;
import com.foros.util.StringUtil;
import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

public class CurrentUserSettingsFilter extends OncePerRequestFilter {

    private static final String LANGUAGE_PARAMETER = "l";

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            ApplicationPrincipal principal = SecurityContext.getPrincipal();

            if (principal != null && !principal.isAnonymous()) {
                User user = userService.getMyUser();

                Locale selectedLocale = getLocale(request, response, user.getLocale());

                CurrentUserSettingsHolder.set(
                        request.getRemoteAddr(),
                        user.getAccount().getTimezone().toTimeZone(),
                        selectedLocale
                );
            } else {
                CurrentUserSettingsHolder.set(
                        request.getRemoteAddr(),
                        TimeZone.getDefault(),
                        getLocale(request, response, null)
                );
            }

            filterChain.doFilter(request, response);
        } finally {
            CurrentUserSettingsHolder.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    /**
     * After requesting FOROS-UI with 'l' parameter save it as http session
     * attribute, and use changed language determination algorithm for not
     * logged use: <li>Request parameter (l)</li> <li>Session attribute (l)</li>
     * <li>Accept-language header</li> <li>English</li>
     *
     *
     * @param request servlet request
     * @param userLocale user locale
     * @return user locale
     */
    private static Locale getLocale(HttpServletRequest request, HttpServletResponse response, Locale userLocale) {
        Locale locale = getParameterLocale(request);

        if (locale != null) {
            saveLocaleToCookie(request, response, locale);
            return locale;
        }

        if (userLocale != null) {
            return userLocale;
        }

        Locale cookieLocale = getCookieLocale(request);

        if (cookieLocale != null) {
            return cookieLocale;
        }

        Locale requestLocale = request.getLocale();

        if (requestLocale != null) {
            return requestLocale;
        }

        return Locale.getDefault();
    }

    private static void saveLocaleToCookie(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        Cookie cookie = new Cookie(LANGUAGE_PARAMETER, locale.toString());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private static Locale getCookieLocale(HttpServletRequest request) {
        CookiesContainer cookies = new CookiesContainer(request.getCookies());
        return parseLocale(cookies.get(LANGUAGE_PARAMETER));
    }

    private static Locale getParameterLocale(HttpServletRequest request) {
        return parseLocale(request.getParameter(LANGUAGE_PARAMETER));
    }

    private static Locale parseLocale(String localeCode) {
        if (StringUtil.isPropertyEmpty(localeCode)) {
            return null;
        }

        String[] localeParts = localeCode.split("_");

        if (localeParts.length == 0 || localeParts.length > 2) {
            return null;
        }

        String language = localeParts[0];

        if(localeParts.length == 1) {
            return new Locale(language);
        } else {
            return new Locale(language, localeParts[1]);
        }
    }

}
