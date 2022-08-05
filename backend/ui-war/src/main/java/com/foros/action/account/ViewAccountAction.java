package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class ViewAccountAction extends BaseActionSupport {

    @EJB
    private AccountService accountService;

    private Long id;

    @ReadOnly
    public String view() {
        Account account = accountService.view(id);

        if (account instanceof AdvertiserAccount) {
            if (((AdvertiserAccount) account).isStandalone()) {
                return "advertiser";
            } else {
                return "agencyAdvertiser";
            }
        } else if (account instanceof AgencyAccount) {
            return "advertiser";
        } else if (account instanceof PublisherAccount) {
            return "publisher";
        } else if (account instanceof IspAccount) {
            return "isp";
        } else if (account instanceof CmpAccount) {
            return "cmp";
        } else if (account instanceof InternalAccount) {
            return "internal";
        }

        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
