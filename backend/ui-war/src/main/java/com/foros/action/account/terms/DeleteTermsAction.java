package com.foros.action.account.terms;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class DeleteTermsAction extends BaseActionSupport {
    @EJB
    private AccountService accountService;
    private Long id;
    private String file;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @ReadOnly
    public String delete() {
        accountService.view(id);
        Account account = accountService.find(id);
        if (!accountService.deleteTerm(account, file)) {
            return INPUT;
        }
        return SUCCESS;
    }
}
