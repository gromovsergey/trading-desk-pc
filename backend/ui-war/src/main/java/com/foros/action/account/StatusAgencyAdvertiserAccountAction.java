package com.foros.action.account;


import com.foros.action.BaseActionSupport;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class StatusAgencyAdvertiserAccountAction extends BaseActionSupport {
    @EJB
    AccountService accountService;

    private long id;

    public String activate() {
        accountService.activateAgencyAdvertiser(id);

        return SUCCESS;
    }

    public String inactivate() {
        accountService.inactivateAgencyAdvertiser(id);

        return SUCCESS;
    }

    public String delete() {
        accountService.deleteAgencyAdvertiser(id);

        return SUCCESS;
    }

    public String undelete() {
        accountService.undeleteAgencyAdvertiser(id);

        return SUCCESS;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
