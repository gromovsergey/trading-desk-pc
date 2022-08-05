package com.foros.action.account;

import com.foros.action.BaseActionSupport;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class StatusAccountAction extends BaseActionSupport {
    @EJB
    AccountService accountService;

    private long id;

    public String activate() {
        accountService.activate(id);

        return SUCCESS;
    }

    public String inactivate() {
        accountService.inactivate(id);

        return SUCCESS;
    }

    public String delete() {
        accountService.delete(id);

        return SUCCESS;
    }

    public String undelete() {
        accountService.undelete(id);

        return SUCCESS;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
