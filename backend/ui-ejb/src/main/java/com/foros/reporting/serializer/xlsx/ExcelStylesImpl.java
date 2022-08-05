package com.foros.reporting.serializer.xlsx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ExcelStylesImpl implements ExcelStyles {
    private Locale locale;
    private ExcelStyles parentStyles;
    private Map<String, Stylist> stylistMap = new HashMap<String, Stylist>();

    public ExcelStylesImpl(Locale locale) {
        this.locale = locale;
    }

    public ExcelStylesImpl(ExcelStyles parentStyles, Locale locale) {
        this.parentStyles = parentStyles;
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Stylist get(Collection<String> names) {

        Collection<Stylist> stylists = new ArrayList<Stylist>(names.size());
        for (String name : names) {
            stylists.add(getStylist(name));
        }

        return new CompositeStylist(stylists);
    }

    private Stylist getStylist(String name) {
        Stylist stylist = stylistMap.get(name);
        if (stylist == null && parentStyles != null) {
            stylist = parentStyles.get(Collections.singleton(name));
        }

        if (stylist == null) {
            throw new IllegalArgumentException("Style is not defined: " + name);
        }

        return stylist;
    }

    public ExcelStylesImpl add(String name, Stylist stylist) {
        stylistMap.put(name, stylist);
        return this;
    }
}
