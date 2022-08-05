package com.foros.action;

import com.foros.util.StringUtil;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TextProviderSupport implements TextProvider {
    private LocaleProvider localeProvider;

    public TextProviderSupport(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    @Override
    public String getText(String key) {
        return StringUtil.getLocalizedString(key, localeProvider.getLocale());
    }

    @Override
    public String getText(String key, String defaultValue) {
        return StringUtil.getLocalizedStringWithDefault(key, defaultValue, localeProvider.getLocale());
    }

    @Override
    public String getText(String key, String defaultValue, String obj) {
        return StringUtil.getLocalizedStringWithDefault(key, defaultValue, localeProvider.getLocale(), obj);
    }

    @Override
    public String getText(String key, List<?> args) {
        return StringUtil.getLocalizedString(key, localeProvider.getLocale(), toArray(args));
    }

    @Override
    public String getText(String key, String[] args) {
        return StringUtil.getLocalizedString(key,localeProvider.getLocale(), args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args) {
        return StringUtil.getLocalizedStringWithDefault(key, defaultValue, localeProvider.getLocale(), toArray(args));
    }

    @Override
    public String getText(String key, String defaultValue, String[] args) {
        return StringUtil.getLocalizedStringWithDefault(key, defaultValue, localeProvider.getLocale(), args);
    }

    @Override
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return getTextWithStack(key, defaultValue, toArray(args), stack);
    }

    @Override
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return getTextWithStack(key, defaultValue, args, stack);
    }

    private String getTextWithStack(String key, String defaultValue, Object[] args, ValueStack stack) {
        Locale locale = localeProvider.getLocale();
        String template = StringUtil.getLocalizedStringWithDefault(key, defaultValue, locale);
        template = TextParseUtil.translateVariables(template, stack);
        return StringUtil.formatMessage(template, locale, args);
    }

    @Override
    public ResourceBundle getTexts(String bundleName) {
        return getTexts();
    }

    @Override
    public ResourceBundle getTexts() {
        return StringUtil.getBundle(localeProvider.getLocale());
    }

    private Object[] toArray(List args) {
        return args != null ? args.toArray() : null;
    }

    @Override
    public boolean hasKey(String key) {
        return getTexts().containsKey(key);
    }

}
