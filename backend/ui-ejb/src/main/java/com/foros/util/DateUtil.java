package com.foros.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    public static void resetFields(Calendar calendar, int ... fields) {
        for (int field : fields) {
            calendar.set(field, 0);
        }
    }

    public static boolean inMonthRange(Date dateFrom, Date dateTo) {
        Calendar from = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        from.setTime(dateFrom);

        Calendar to = Calendar.getInstance();
        to.setTime(dateTo);

        // from first day of a month to a date in same month
        return from.get(Calendar.DAY_OF_MONTH) == 1 && from.get(Calendar.MONTH) == to.get(Calendar.MONTH) && from.get(Calendar.YEAR) == to.get(Calendar.YEAR);
    }
}
