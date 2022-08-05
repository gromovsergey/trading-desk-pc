package com.foros.ui.authentication.spring.filter;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import java.io.IOException;

public class LocaleFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CurrentUserSettingsHolder.Settings settings = CurrentUserSettingsHolder.getSettings();
        Config.set(((HttpServletRequest) request).getSession(), Config.FMT_LOCALE, settings.getLocale());
        chain.doFilter(request, response);
    }
}
