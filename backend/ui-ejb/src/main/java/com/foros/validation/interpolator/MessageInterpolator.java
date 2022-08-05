package com.foros.validation.interpolator;

import java.util.Locale;

public interface MessageInterpolator {

    String interpolate(MessageTemplate template, Locale locale);

    String interpolate(MessageTemplate template);

}
