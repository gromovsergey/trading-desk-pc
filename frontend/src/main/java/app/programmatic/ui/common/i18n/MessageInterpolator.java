package app.programmatic.ui.common.i18n;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class MessageInterpolator {
    static MessageInterpolator DEFAULT_MESSAGE_INTERPOLATOR = new MessageInterpolator(LOCALE_RU);

    private Locale defaultLocale;

    public MessageInterpolator(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public static MessageInterpolator getDefaultMessageInterpolator() {
        return DEFAULT_MESSAGE_INTERPOLATOR;
    }

    public String interpolate(Locale locale, String template, Object... parameters) {
        return interpolateImpl(locale, template, parameters);
    }

    public String interpolate(String template, Object... parameters) {
        return interpolateImpl(defaultLocale, template, parameters);
    }

    private String interpolateImpl(Locale locale, String messageTemplate, Object[] parameters) {
        Object[] interpolatedParameters = interpolateParameters(locale, parameters);
        return getLocalizedStringWithDefault(messageTemplate, locale, messageTemplate, interpolatedParameters);
    }

    private Object[] interpolateParameters(Locale locale, Object[] parameters) {
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

        return getLocalizedStringWithDefault(stripped, locale, stripped, parameter);
    }

    private static String getLocalizedStringWithDefault(String key, Locale locale, String defaultValue, Object... args) {
        String result = getCachedLocalizedString(key, locale);
        if (result == null) {
            return defaultValue;
        }

        return formatMessage(result, locale, args);
    }

    private static String getCachedLocalizedString(final String key, final Locale locale) {
        if (locale == null) {
            return null;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    private static String formatMessage(String pattern, Locale locale, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }

        MessageFormat format = new MessageFormat(pattern, locale);

        Format[] formats = format.getFormats();
        if (formats.length == 0) {
            return pattern;
        }

        return format.format(args);
    }
}
