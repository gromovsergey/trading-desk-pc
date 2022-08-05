package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.security.AccountRole;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class AccountRoleXmlAction extends AbstractXmlAction<AccountRole> {
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

    public AccountRole generateModel() throws ProcessException {
        return accountService.view(Long.parseLong(getAccountId())).getRole();
    }

}
