package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.Timezone;
import com.foros.session.account.AccountService;
import com.foros.util.StringUtil;

import javax.ejb.EJB;

/**
 * @author Vladimir
 */
public class AccountTimeZoneXmlAction extends AbstractXmlAction<Timezone> {
    @EJB
    private AccountService accountService;

    private String accountId;

    private String timeZoneName;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public Timezone generateModel() throws ProcessException {
        if (!StringUtil.isPropertyEmpty(timeZoneName)) {
            Timezone timezone = new Timezone();
            timezone.setKey(timeZoneName);
            return timezone;
        }

        Long accId = null;

        try {
            accId = StringUtil.isPropertyEmpty(accountId) ? null : Long.parseLong(accountId);
        } catch (Exception ignored) {

        }

        if (accountId == null) {
            return accountService.getMyAccount().getTimezone();
        }

        return accountService.getAccountTimeZone(accId);
    }

}
