package com.foros.validation.validator;

import com.foros.validation.interpolator.MessageInterpolator;
import com.foros.validation.interpolator.MessageTemplate;

import java.util.Locale;

public class NullInterpolator implements MessageInterpolator {
    public static final MessageInterpolator INSTANCE = new NullInterpolator();

    @Override
    public String interpolate(MessageTemplate template, Locale locale) {
        return template.getTemplate();
    }

    @Override
    public String interpolate(MessageTemplate template) {
        return template.getTemplate();
    }
}
