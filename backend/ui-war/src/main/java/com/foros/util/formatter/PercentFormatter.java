package com.foros.util.formatter;

import java.text.ParseException;

public class PercentFormatter implements FieldFormatter {

    private NumberFormatter nf = new NumberFormatter();

    public String getString(Object o) {
        if (o == null) {
            return "";
        }

        FloatFormatter ff = new FloatFormatter();
        return ff.getString(o) + "%";
    }
    
    public double parse(String str) throws ParseException {
        if (str != null) {
            int indx = str.indexOf('%');
            if (indx > 0) {
                str = str.substring(0, indx);
            }
        }

        return nf.parse(str).doubleValue();
    }
}
