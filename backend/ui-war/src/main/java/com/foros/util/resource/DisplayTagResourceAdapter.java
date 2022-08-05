package com.foros.util.resource;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.StringUtil;

import org.displaytag.localization.I18nResourceProvider;
import org.displaytag.localization.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.util.Locale;

public class DisplayTagResourceAdapter implements I18nResourceProvider, LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return CurrentUserSettingsHolder.getLocale();
    }

    public String getResource(String resourceKey, String defaultValue, Tag tag, PageContext context) {
        return StringUtil.getLocalizedStringWithDefault(resourceKey, defaultValue);
    }
}
