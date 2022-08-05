package com.foros.util;

import com.foros.model.RegularCheckable;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RegularChecksUtil {

    public static String getCheckStatusCaption(RegularCheckable check, Locale locale, boolean hourlyCheck) {
        // check status OK/Due X
        StringBuilder sb = new StringBuilder();
        if (check.getNextCheckDate() == null) {
            // next check cannot be null
        } else {
            double hoursAgo = (double) (new Date().getTime() - check.getNextCheckDate().getTime()) / 3600000;
            if (hoursAgo < 0) {
                sb.append(StringUtil.getLocalizedString("checks.checkStatus.OK"));
            } else {
                sb.append("<font color=\"red\">");
                sb.append(StringUtil.getLocalizedString("checks.checkStatus.due.param", getDueCaption(hoursAgo, hourlyCheck)));
                sb.append("</font>");
            }
            sb.append(", ");
        }
        // previous check
        if (check.getLastCheckDate() == null) {
            sb.append(StringUtil.getLocalizedString("checks.checkStatus.noPreviousCheck"));
        } else {
            String checkUser = check.getCheckUser().getFullName();
            String date = DateHelper.formatDateTime(new Date(check.getLastCheckDate().getTime()), TimeZone.getTimeZone("GMT"), locale);
            sb.append(StringUtil.getLocalizedString("checks.checkStatus.lastCheckBy", checkUser, date));

            // next check
            if (check.getNextCheckDate() != null && check.getNextCheckDate().after(new Date())) {
                sb.append(". ");
                int hoursNext = (int) Math.ceil((double) (check.getNextCheckDate().getTime() - new Date().getTime()) / 3600000);
                if (hoursNext < 24) {
                    sb.append(StringUtil.getLocalizedString("checks.checkStatus.nextCheckIsDueIn.hours", hoursNext));
                } else {
                    sb.append(StringUtil.getLocalizedString("checks.checkStatus.nextCheckIsDueIn.days", (int) Math.ceil(hoursNext / 24)));
                }
            }
        }
        return sb.toString();
    }

    public static String getDueCaption(double hoursAgo, boolean hourlyCheck) {
        if (hoursAgo < 0) {
            return "OK";
        }
        if (hoursAgo <= 1 && hourlyCheck) {
            return StringUtil.getLocalizedString("checks.checkStatus.now");
        }
        if (hoursAgo <= 24 && !hourlyCheck) {
            return StringUtil.getLocalizedString("checks.checkStatus.today");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<font color=\"red\">");
        if (hoursAgo < 24) {
            sb.append(StringUtil.getLocalizedString("checks.checkStatus.hoursAgo", (int) Math.floor(hoursAgo)));
        } else {
            sb.append(StringUtil.getLocalizedString("checks.checkStatus.daysAgo", (int) Math.floor(hoursAgo / 24)));
        }
        sb.append("</font>");
        return sb.toString();
    }
}
