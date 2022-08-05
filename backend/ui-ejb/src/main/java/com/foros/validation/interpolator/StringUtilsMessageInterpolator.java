package com.foros.validation.interpolator;

import com.foros.util.StringUtil;
import java.util.Locale;

public class StringUtilsMessageInterpolator implements MessageInterpolator {

    private Locale defaultLocale;

    public StringUtilsMessageInterpolator(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public String interpolate(MessageTemplate template, Locale locale) {
        String messageTemplate = template.getTemplate();

        return interpolate(locale, messageTemplate, template.getParameters());
    }

    @Override
    public String interpolate(MessageTemplate template) {
        return interpolate(template, defaultLocale);
    }

    private String interpolate(Locale locale, String messageTemplate, Object[] parameters) {
        if (messageTemplate == null) {
            return null;
        }

        Object[] interpolatedParameters = interpolateParameters(locale, parameters);
        return StringUtil.getLocalizedStringWithDefault(messageTemplate, messageTemplate, locale, interpolatedParameters);
    }

    private Object[] interpolateParameters(Locale locale, Object[] parameters) {
        if (parameters == null) {
            return null;
        }

        Object[] result = new Object[parameters.length];

        int index = 0;
        for (Object parameter : parameters) {
            if (parameter instanceof String) {
                result[index++] = interpolateParameter(locale, (String) parameter);
            } else {
                result[index++] = parameter;
            }
        }

        return result;
    }

    private String interpolateParameter(Locale locale, String parameter) {
        if (parameter == null) {
            return null;
        }

        // can't be key in curly brackets
        if (parameter.length() <= 2) {
            return parameter;
        }

        // is it looks like template?
        if (parameter.charAt(0) != '{' || parameter.charAt(parameter.length() - 1) != '}') {
            return parameter;
        }

        String stripped = parameter.substring(1, parameter.length() - 1);

        return StringUtil.getLocalizedStringWithDefault(stripped, parameter, locale);
    }

}
