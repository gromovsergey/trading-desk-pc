package com.foros.util.formatter;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import java.text.NumberFormat;

public class FloatFormatter implements FieldFormatter {
    private NumberFormat numberFormat;

    public FloatFormatter() {
        this(2);
    }

    public FloatFormatter(int scale) {
        this.numberFormat = CurrentUserSettingsHolder.getNumberFormat();
        this.numberFormat.setMaximumFractionDigits(scale);
        this.numberFormat.setMinimumFractionDigits(scale);
    }

    public String getString(Object o) {
        if (o == null) {
            return "";
        }

        if (!(o instanceof Number)) {
            try {
                o = new Double(o.toString());
            } catch (Exception e) {
                return o.toString();
            }
        }

        return numberFormat.format(((Number)o).doubleValue());
    }
}
