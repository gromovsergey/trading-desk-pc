package com.foros.util.csv;

import com.foros.model.Status;
import com.foros.model.campaign.RateType;
import com.foros.model.time.TimeSpan;
import com.foros.model.time.TimeUnit;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.StringUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

public class BaseBulkHelper {

    public static Long parseInteger(String value) throws ParseException {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        DecimalFormat integerFormat = (DecimalFormat) DecimalFormat.getIntegerInstance(locale);

        if (StringUtil.isPropertyEmpty(value)) {
            return null;
        }

        int i;
        if ((i = value.indexOf(integerFormat.getDecimalFormatSymbols().getExponentSeparator())) != -1) {
            throw new ParseException(value + " is not an integer", i);
        }

        ParsePosition pos = new ParsePosition(0);
        Long number = (Long) integerFormat.parse(value, pos);
        if (pos.getIndex() != value.length()) {
            throw new ParseException(value + " is not an integer", pos.getIndex());
        }

        return number;
    }

    public static Status parseStatus(String string) {
        return string == null ? null : Status.valueOf(string.toUpperCase());
    }

    public static RateType parseRateType(String string) {
        return string == null ? null : RateType.valueOf(string.toUpperCase());
    }

    public static TimeSpan parseTimeSpan(String string) throws ParseException {
        if (string == null) {
            return null;
        }
        char metric = string.charAt(string.length() - 1);
        TimeUnit timeUnit = TimeUnit.valueOf(Character.toUpperCase(metric));
        Long value = parseInteger(string.substring(0, string.length() - 1));

        return new TimeSpan(value, timeUnit);
    }
}