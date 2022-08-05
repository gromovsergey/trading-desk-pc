package com.foros.web.taglib;

import com.foros.model.security.User;
import com.foros.reporting.serializer.formatter.LocalDateTimeValueFormatter;
import com.foros.reporting.serializer.formatter.LocalDateValueFormatter;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.ServiceLocator;
import com.foros.session.security.UserService;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class ReportHelper {

    public static User currentUserInfo() {
        UserService userService = ServiceLocator.getInstance().lookup(UserService.class);
        User user = userService.getMyUser();

        return user;
    }

    public static boolean isExportParameter(String parameter) {
        return !parameter.startsWith("paging.");
    }

    public static String formatLocalDate(LocalDate date) {
        return LocalDateValueFormatter.formatHtml(date, CurrentUserSettingsHolder.getLocale());
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return LocalDateTimeValueFormatter.formatHtml(dateTime, CurrentUserSettingsHolder.getLocale());
    }
}
