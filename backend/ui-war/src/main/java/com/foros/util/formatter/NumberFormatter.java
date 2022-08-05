package com.foros.util.formatter;

import com.foros.security.currentuser.CurrentUserSettingsHolder;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberFormatter implements FieldFormatter {

    private NumberFormat nf = CurrentUserSettingsHolder.getNumberFormat();

    public String getString(Object o) {
        if (o == null) {
            return "";
        }

        if (!(o instanceof Number)) {
            return o.toString();
        }

        BigDecimal bd = new BigDecimal(((Number)o).doubleValue());
        bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);

        return nf.format(bd.doubleValue());
    }

    public Number parse(String val) throws ParseException{
        return  nf.parse(val);
    }

}
