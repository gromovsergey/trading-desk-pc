package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.DateInfo;
import com.foros.model.account.Account;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

/**
 * User: Nitin Afre
 * Date: Jul 13, 2009
 * Time: 4:24:42 PM
 */
public class DateTimeFormatXmlAction extends AbstractXmlAction<DateInfo> {
    @EJB
    private AccountService accountService;
    private String accountId;

    @RequiredFieldValidator(key = "errors.required", message = "accountId")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    protected DateInfo generateModel() throws ProcessException {
        Account account = accountService.view(StringUtil.convertToLong(accountId));
        
        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());

        Date date = new Date();
        String dateString = StringUtil.trimProperty(DateHelper.formatDate(date, DateFormat.SHORT, timeZone, locale));
        String timeString = StringUtil.trimProperty(DateHelper.formatTime(date, DateFormat.SHORT, timeZone, locale));

        DateInfo dateInfo = new DateInfo(dateString, timeString);

        return dateInfo;
    }
}
