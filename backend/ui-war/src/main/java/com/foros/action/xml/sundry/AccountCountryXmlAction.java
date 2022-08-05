package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.model.account.Account;
import com.foros.session.account.AccountService;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class AccountCountryXmlAction extends AbstractXmlAction<Account> {
    @EJB
    private AccountService service;
    String accountId;

    @RequiredStringValidator(key = "errors.required", message = "countryCode")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    protected Account generateModel() throws ProcessException {
        Account account = service.find(new Long(getAccountId()));
        return account;
    }
}
