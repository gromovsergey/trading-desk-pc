package com.foros.reporting.serializer.xlsx;

import java.util.Collection;
import java.util.Locale;

public interface ExcelStyles {
    Locale getLocale();

    Stylist get(Collection<String> names);
}
