package com.foros.action.reporting.userAgents;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.util.DateHelper;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewUserAgentsReportingAction extends BaseActionSupport {

    private String dateDisplay;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'userAgents'")
    public String view() throws Exception {
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = CurrentUserSettingsHolder.getTimeZone();

        dateDisplay = DateHelper.formatDate(new Date(), timeZone, locale);

        return "success";
    }

    public String getDateDisplay() {
        return dateDisplay;
    }

    public void setDateDisplay(String dateDisplay) {
        this.dateDisplay = dateDisplay;
    }
}
