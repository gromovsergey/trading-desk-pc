package com.foros.action.account.terms;

import com.foros.action.download.DownloadFileActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

public class DownloadTermsAction extends DownloadFileActionSupport {
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
    @Override
    public String download() {
        Account account = accountService.find(id);
        setContentSource(accountService.getTermContent(account, file));
        setTargetFile(file);
        return SUCCESS;
    }
}
